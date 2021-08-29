package io.georgeous.petmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PetCommand implements CommandExecutor, TabCompleter {

    public PetCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only!");
            return true;
        }
        Player player = (Player) sender;

        switch (args[0]) {
            case "release":
                PetManager.releaseAllPets(player);
                break;
            case "passon":
                Player otherPlayer = findClosestPlayer(player, 5d);
                PetManager.passPets(player, otherPlayer);
                break;
            case "count":
                player.sendMessage("Pets: " + PetManager.getPetCount(player));
                break;
            case "save":
                //petManager.save();
                break;
            case "load":
                //petManager.restore(player);
                break;
            case "entries":
                //petManager.restore(player);
                player.sendMessage(PetManager.getEntries() + "");

                break;
            default:
                player.sendMessage("Command not found");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> l = new ArrayList<String>();
        if (cmd.getName().equalsIgnoreCase("pets") && args.length >= 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                l.add("release");
                l.add("passon");
                l.add("count");
                l.add("save");
                l.add("load");
                l.add("entries");
                return l;
            }
        }
        return l;
    }

    public Player findClosestPlayer(Player player, Double range) {
        Player closest = null;

        double closestDist = 99999;
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.getUniqueId().equals(player.getUniqueId())) { // Not Self
                double dist = player.getLocation().distance(other.getLocation());
                if (dist < range) { // In range
                    if (dist < closestDist) { // Nearest
                        closest = other;
                        closestDist = dist;
                    }
                }
            }
        }
        return closest;
    }
}