package io.georgeous.petmanager;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PetListener implements Listener {

    @EventHandler
    public void onTaming(EntityTameEvent event){
        LivingEntity pet = event.getEntity();
        AnimalTamer owner = event.getOwner();

        if(owner instanceof Player){
            PetManager.addPet((Player) owner,pet);
        }
    }

    @EventHandler
    public void onBreeding(EntityBreedEvent event){
        LivingEntity newBornAnimal = event.getEntity();
        LivingEntity breeder = event.getBreeder();

        if(breeder instanceof Player){
            if(newBornAnimal instanceof Wolf || newBornAnimal instanceof Cat){
                PetManager.addPet((Player) breeder, newBornAnimal);
            }
        }
    }

    @EventHandler
    public void onPetDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        if(!(entity instanceof Tameable))
            return;

        AnimalTamer owner = ((Tameable) entity).getOwner();
        if(!(owner instanceof Player))
            return;

        PetManager.removePet((Player) owner, entity);
    }
}
