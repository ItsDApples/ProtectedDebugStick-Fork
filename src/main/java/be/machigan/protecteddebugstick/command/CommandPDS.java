package be.machigan.protecteddebugstick.command;

import be.machigan.protecteddebugstick.ProtectedDebugStick;
import be.machigan.protecteddebugstick.def.DebugStick;
import be.machigan.protecteddebugstick.property.Property;
import be.machigan.protecteddebugstick.def.RecipeHandler;
import be.machigan.protecteddebugstick.utils.Message;
import be.machigan.protecteddebugstick.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandPDS implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!commandSender.hasPermission("pds.command.use")) {
            Player player = (Player) commandSender;
            Message.getMessage("Command.PDS.NoPerm", (Player) commandSender, false)
                    .replace("{perm}", "pds.command.use")
                    .send(player);
            return true;
        }
        if (strings.length == 0) {
            Message.getMessage("Command.PDS.NotEnoughArg", true)
                    .send(commandSender);
            return true;
        }

        if (strings[0].equalsIgnoreCase("give")) {
            if (!commandSender.hasPermission("pds.command.give")) {
                Player player = (Player) commandSender;
                Message.getMessage("Command.PDS.Arg.Give.NoPerm", player, false)
                        .replace("{perm}", "pds.command.give")
                        .send(player);
                return true;
            }

            if (strings.length < 2) {
                Message.getMessage("Command.PDS.Arg.Give.NoPlayer")
                        .send(commandSender);
                return true;
            }

            Player player = Bukkit.getPlayer(strings[1]);
            if (player == null) {
                Message.getMessage("Command.PDS.Arg.Give.UnknownPlayer")
                        .replace("{player}", strings[1])
                        .send(commandSender);
                return true;
            }
            if (strings.length < 3) {
                Message.getMessage("Command.PDS.Arg.Give.WhatToGive")
                        .replace("{player}", player.getName())
                        .send(commandSender);
                return true;
            }

            switch (strings[2].toLowerCase()) {
                case "basic":
                    if (strings.length < 4) {
                        Message.getMessage("Command.PDS.Arg.Give.NoDurability")
                                .replace("{player}", player.getName())
                                .send(commandSender);
                        return true;
                    }
                    try {
                        int durability = Integer.parseInt(strings[3]);
                        if (durability <= 0) {
                            throw new NumberFormatException("Durability below or equal to 0");
                        }
                        player.getInventory().addItem(DebugStick.getDebugStick(durability));
                        break;
                    } catch (NumberFormatException ignored) {
                        Message.getMessage("Command.PDS.Arg.Give.InvalidDurability")
                                .replace("{player}", player.getName())
                                .replace("{durability}", strings[3])
                                .send(commandSender);
                        return true;
                    }

                case "infinity":
                    player.getInventory().addItem(DebugStick.getInfinityDebugStick());
                    break;

                case "inspector":
                    player.getInventory().addItem(DebugStick.inspector);
                    break;

                default:
                    Message.getMessage("Command.PDS.Arg.Give.UnknownItem")
                            .replace("{player}", player.getName())
                            .replace("{item}", strings[2])
                            .send(commandSender);
                    return true;
            }
            Message.getMessage("Command.PDS.Arg.Give.Success")
                    .replace("{player}", player.getName())
                    .replace("{item}", strings[2])
                    .send(commandSender);
            return true;

        }

        if (strings[0].equalsIgnoreCase("reload-config")) {
            if (!commandSender.hasPermission("pds.command.reloadConfig")) {
                Player player = (Player) commandSender;
                Message.getMessage("Command.PDS.Arg.ReloadConfig.NoPerm", player, false)
                        .replace("{perm}", "pds.command.reloadConfig")
                        .send(player);
                return true;
            }
            ProtectedDebugStick.getInstance().reloadConfig();
            ProtectedDebugStick.config = ProtectedDebugStick.getInstance().getConfig();
            DebugStick.init();
            Property.init();
            RecipeHandler.register();
            try {
                ProtectedDebugStick.prefix = Tools.replaceColor(ProtectedDebugStick.config.getString("prefix"));
            } catch (NullPointerException ignored) {
                ProtectedDebugStick.prefix = Tools.replaceColor(ProtectedDebugStick.PREFIX);
            }

            Message.getMessage("Command.PDS.Arg.ReloadConfig.Success")
                    .send(commandSender);
            return true;
        }

        Message.getMessage("Command.PDS.NoCommandFound", false)
                .replace("{arg}", strings[0])
                .send(commandSender);
        return true;
    }
}
