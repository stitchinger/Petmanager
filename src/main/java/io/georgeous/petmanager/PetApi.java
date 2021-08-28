package io.georgeous.petmanager;

import org.bukkit.entity.*;

public class PetApi {

    private static PetManager pm;

    public static void init(PetManager p){
        pm = p;
    }


    public static void releasePets(Player player){
        pm.releaseAllPets(player);
    }

    public static void newWulf(Player player){
        Wolf w = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        pm.addPet(player, w);
    }
}
