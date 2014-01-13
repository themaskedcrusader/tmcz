/*
 * Copyright 2013 Topher Donovan (themaskedcrusader.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.themaskedcrusader.tmcz.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHelp extends CommandProcessor {

    public CommandHelp(JavaPlugin plugin) {
        super(plugin);
    }

    public static void baseCommandHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Current Commands:");
        sender.sendMessage(commandString("help", "Display this help"));

        if (sender instanceof Player) {
            sender.sendMessage(commandString("spawn", "Spawn into world and start the game"));
            sender.sendMessage(commandString("kills", "Display your kills for the current game"));
            sender.sendMessage(commandString("die", "Snuff out poor Steve's life"));
            sender.sendMessage(commandString("rejoin", "Rejoin game with current inventory if booted by error"));
        }

        if (authorized(sender) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(commandString("addSpawn", "Add current location to TMCz game spawns list"));
            sender.sendMessage(commandString("setWorldSpawn", "Set your current location as world default spawn location"));
            sender.sendMessage(commandString("worldName", "Display your current internal world name"));
        }

        if (authorized(sender)) {
            sender.sendMessage(commandString("disable", "Disables the plugin while in bukkit"));
            sender.sendMessage(commandString("reload", "Reloads only this plugin while in bukkit"));
        }
    }

    private static String commandString(String command, String text) {
        String toReturn = ChatColor.YELLOW + "  /minez "  + command;
        toReturn += ChatColor.AQUA + " : " + text;
        return toReturn;
    }
}
