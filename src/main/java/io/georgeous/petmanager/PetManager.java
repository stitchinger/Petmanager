package io.georgeous.petmanager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.util.*;

public class PetManager extends JavaPlugin {

    private static HashMap<String, ArrayList<LivingEntity>> ownerPets = new HashMap<>();


    @Override
    public void onEnable(){
        registerCommands();
        registerEvents();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if(playerDataExists(player)) {
                restore(player);
            }
        }
    }

    @Override
    public void onDisable(){
        save();
    }

    private void registerCommands() {
        PetCommand petCommand = new PetCommand();
        getServer().getPluginCommand("pets").setExecutor(petCommand);
        getCommand("pets").setTabCompleter(petCommand);

    }

    private void registerEvents() {
        PetListener petListener = new PetListener();
        getServer().getPluginManager().registerEvents(petListener, this);
    }

    private void save() {
        if (ownerPets.isEmpty())
            return;
        for (Map.Entry<String, ArrayList<LivingEntity>> entry : ownerPets.entrySet()) { // Every Petowner
            if (entry.getValue() != null) {
                FileConfiguration config = this.getConfig();
                String uuid = entry.getKey();

                List<String> uuidList = new ArrayList<>();

                for (LivingEntity pet : entry.getValue()) { // Every pet
                    if (pet != null) {
                        String petUuid = pet.getUniqueId().toString();
                        uuidList.add(petUuid);
                    }
                }
                config.set("data.pets." + uuid, uuidList);
                this.saveConfig();
            }
        }
    }

    private void restore(Player player) {
        String uuid = player.getUniqueId().toString();
        FileConfiguration config = this.getConfig();
        //ConfigurationSection configSection = config.getConfigurationSection("data.pets." + uuid);
        List<String> petsUuid = config.getStringList("data.pets." + uuid);

        for (String id : petsUuid) {
            addPet(player, (LivingEntity) Bukkit.getEntity(UUID.fromString(id)));
        }
        //config.set("data.player." + uuid + ".role", null);
        this.saveConfig();
    }

    public static void addPet(Player p, LivingEntity pet) {
        String uuid = p.getUniqueId().toString();
        addPet(uuid, pet);
    }

    public static void addPet(String uuid, LivingEntity pet) {
        if (ownerPets.get(uuid) == null) {
            ownerPets.put(uuid, new ArrayList<>());
        }
        ownerPets.get(uuid).add(pet);
    }

    public static void removePet(Player player, LivingEntity pet) {
        String uuid = player.getUniqueId().toString();
        if (ownerPets.get(uuid) == null) {
            return;
        }
        ownerPets.get(uuid).remove(pet);
    }

    public static void releasePet(Entity entity) {
        if (entity instanceof Sittable) {
            ((Sittable) entity).setSitting(false);
        }
        if (entity instanceof Tameable) {
            ((Tameable) entity).setTamed(false);
            ((Tameable) entity).setOwner(null);
        }
    }

    public static void releaseAllPets(Player player) {
        String uuid = player.getUniqueId().toString();
        if (ownerPets.get(uuid) == null){
            player.sendMessage("You dont have any pets");
            return;
        }

        int count = 0;

        for (Iterator<LivingEntity> iterator = ownerPets.get(uuid).iterator(); iterator.hasNext(); ) {
            LivingEntity e = iterator.next();

            if (e instanceof Tameable) {
                if (e instanceof Sittable) {
                    ((Sittable) e).setSitting(false);
                }
                ((Tameable) e).setTamed(false);
                ((Tameable) e).setOwner(null);

                count++;
                iterator.remove();
            }
        }
        player.sendMessage("You released " + count + " pets");
    }

    public static void passPets(Player owner, Player receiver) {
        String uuid = owner.getUniqueId().toString();

        if (ownerPets.get(uuid) == null || ownerPets.get(uuid).size() == 0) {
            owner.sendMessage("You dont have any pets");
            return;
        }
        if (receiver == null) {
            owner.sendMessage("There are no other players nearby");
            return;
        }
        for (Iterator<LivingEntity> iterator = ownerPets.get(uuid).iterator(); iterator.hasNext(); ) {
            LivingEntity entity = iterator.next();

            if (entity instanceof Tameable) {
                //ownerPets.get(owner).remove(entity);
                ((Tameable) entity).setOwner(receiver);
                addPet(receiver, entity);
                iterator.remove();
            }
        }
    }

    public static int getPetCount(Player owner) {
        String uuid = owner.getUniqueId().toString();
        if (ownerPets.get(uuid) == null) {
            return 0;
        }
        return ownerPets.get(uuid).size();
    }

    private boolean playerDataExists(Player player) {
        return this.getConfig().contains("data.pets." + player.getUniqueId().toString());
    }

    public static int getEntries(){
       return ownerPets.size();
    }
}
