package net.Indyuce.suddendeath.command;

import net.Indyuce.suddendeath.Feature;
import net.Indyuce.suddendeath.SuddenDeath;
import net.Indyuce.suddendeath.gui.AdminView;
import net.Indyuce.suddendeath.gui.Status;
import net.Indyuce.suddendeath.player.PlayerData;
import net.Indyuce.suddendeath.util.CustomItem;
import net.Indyuce.suddendeath.util.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SuddenDeathStatusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {

            // open gui
            if (!(sender instanceof Player))
                return true;

            Player player = (Player) sender;
            if (!sender.hasPermission("suddendeath.status")) {
                player.sendMessage(ChatColor.RED + SuddenDeath.plugin.messages.getConfig().getString("not-enough-perms"));
                return true;
            }
            new Status(player).open();
            return true;
        }

        if (!sender.hasPermission("suddendeath.op")) {
            sender.sendMessage(ChatColor.RED + SuddenDeath.plugin.messages.getConfig().getString("not-enough-perms"));
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------[" + ChatColor.LIGHT_PURPLE + " Sudden Death Help Page " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]---------------");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "<>" + ChatColor.GRAY + " = required");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "()" + ChatColor.GRAY + " = optional");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "..." + ChatColor.GRAY + " = multiple args support");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds " + ChatColor.WHITE + "shows your status.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds admin " + ChatColor.WHITE + "opens the admin GUI.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds help " + ChatColor.WHITE + "displays the help page.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds give <item> (player) (amount) " + ChatColor.WHITE + "gives a player an item.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds itemlist " + ChatColor.WHITE + "displays the item list.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds reload " + ChatColor.WHITE + "reloads the config file.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds clean " + ChatColor.WHITE + "removes all negative effects (Bleeding...).");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/sds start <event> " + ChatColor.WHITE + "starts an event.");
        }

        if (args[0].equals("clean")) {
            if (args.length < 2 && !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Please specify a player to use this command.");
                return true;
            }

            // target
            Player target = args.length < 2 ? Bukkit.getPlayer(sender.getName()) : Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Couldn't find player called " + args[1] + ".");
                return true;
            }

            PlayerData data = PlayerData.get(target);
            if (data.isInfected())
                data.setInfected(false);
            if (data.isBleeding())
                data.setBleeding(false);
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (!(sender instanceof Player))
                return true;

            Player player = (Player) sender;
            if (args.length < 2)
                return true;

            Feature feature = null;
            try {
                feature = Feature.valueOf(args[1].toUpperCase().replace("-", "_"));
                Validate.isTrue(feature.isEvent());
            } catch (IllegalArgumentException exception) {
                sender.sendMessage(ChatColor.RED + "Could not find event called " + args[1].toUpperCase().replace("-", "_") + ".");
                return true;
            }

            player.getWorld().setTime(14000);
            SuddenDeath.plugin.getEventManager().applyStatus(player.getWorld(), feature.generateWorldEventHandler(player.getWorld()));
            String message = ChatColor.DARK_RED + "" + ChatColor.ITALIC + Utils.msg(feature.getPath());
            for (Player online : player.getWorld().getPlayers()) {
                online.sendMessage(message);
                online.sendTitle("", message, 10, 40, 10);
                online.playSound(online.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 666, 0);
            }
        }

        if (args[0].equalsIgnoreCase("admin"))
            if (sender instanceof Player)
                new AdminView((Player) sender).open();

        if (args[0].equalsIgnoreCase("reload")) {
            SuddenDeath.plugin.reloadConfigFiles();
            SuddenDeath.plugin.reloadConfig();
            for (Feature feature : Feature.values())
                feature.updateConfig();
            for (CustomItem item : CustomItem.values())
                item.update(SuddenDeath.plugin.items.getConfig().getConfigurationSection(item.name()));
            sender.sendMessage(ChatColor.YELLOW + SuddenDeath.plugin.getName() + " " + SuddenDeath.plugin.getDescription().getVersion() + " reloaded.");
        }

        if (args[0].equalsIgnoreCase("itemlist")) {
            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " Sudden Death Items " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
            sender.sendMessage("");
            if (sender instanceof Player)
                for (CustomItem item : CustomItem.values())
                    sender.spigot().sendMessage(new ComponentBuilder(item.getName())
                            .color(net.md_5.bungee.api.ChatColor.WHITE)
                            .append(" (")
                            .color(net.md_5.bungee.api.ChatColor.GRAY)
                            .append(item.name())
                            .color(net.md_5.bungee.api.ChatColor.WHITE)
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sds give " + item.name()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to obtain " + item.getName())))
                            .append(") ")
                            .color(net.md_5.bungee.api.ChatColor.GRAY)
                            .create());
            else
                for (CustomItem item : CustomItem.values())
                    sender.sendMessage(" * " + ChatColor.WHITE + item.getName() + ChatColor.GRAY + " (" + ChatColor.WHITE + item.name() + ChatColor.GRAY + ")");
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 3 && !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Please specify a player to use this command.");
                return true;
            }

            // item
            ItemStack i = null;
            try {
                i = CustomItem.valueOf(args[1].replace("-", "_").toUpperCase()).a();
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Couldn't find the item called " + args[1].replace("-", "_").toUpperCase() + ".");
                return true;
            }

            // amount
            if (args.length > 3) {
                try {
                    i.setAmount((int) Double.parseDouble(args[3]));
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + args[3] + " is not a valid number.");
                    return true;
                }
            }

            // target
            Player target = Bukkit.getPlayer(sender.getName());
            if (args.length > 2) {
                if (Bukkit.getPlayer(args[2]) != null)
                    target = Bukkit.getPlayer(args[2]);
                else {
                    sender.sendMessage(ChatColor.RED + "Couldn't find player called " + args[2] + ".");
                    return true;
                }
            }

            // message
            if (sender != target)
                sender.sendMessage(ChatColor.YELLOW + Utils.msg("give-item").replace("#item#", Utils.displayName(i)).replace("#player#", target.getName()).replace("#amount#", (i.getAmount() > 1 ? " x" + i.getAmount() : "")));
            target.sendMessage(ChatColor.YELLOW + Utils.msg("receive-item").replace("#item#", Utils.displayName(i)).replace("#amount#", (i.getAmount() > 1 ? " x" + i.getAmount() : "")));

            // give item
            if (target.getInventory().firstEmpty() == -1) {
                target.getWorld().dropItem(target.getLocation(), i);
                return true;
            }
            target.getInventory().addItem(i);
        }

        return true;
    }
}
