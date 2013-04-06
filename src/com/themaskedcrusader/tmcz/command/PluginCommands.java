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

import com.themaskedcrusader.tmcz.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PluginCommands extends CommandProcessor {

    public PluginCommands(Plugin plugin) {
        super(plugin);
    }

    public static void disablePlugin(CommandSender sender) {
        if ((sender instanceof Player && authorized((Player) sender)) || sender instanceof ConsoleCommandSender) {
            sender.sendMessage("TMCz Shut Down...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public static void reloadMe(CommandSender sender) {
        if ((sender instanceof Player && authorized((Player) sender)) || sender instanceof ConsoleCommandSender) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.AQUA + "TMCz Config Reloaded!");
        }
    }
}
