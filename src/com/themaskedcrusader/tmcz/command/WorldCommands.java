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

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.modules.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class WorldCommands extends CommandProcessor {

    public WorldCommands(JavaPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("unchecked")
    public static void addNewSpawn(Player player) {
        if (authorized(player) && WorldUtils.isAllowed(player.getWorld(), Settings.get())) {
            List<String> locations =  (List<String>) Settings.getConfig().getList(Game.STATIC);
            int newX = player.getLocation().getBlockX();
            int newY = player.getLocation().getBlockY();
            int newZ = player.getLocation().getBlockZ();
            String newLocation = newX + "|" + newY + "|" + newZ;

            if (locations == null) {
                locations = new ArrayList<String>();
            }

            locations.add(newLocation);
            Settings.getConfig().set(Game.STATIC, locations);
            Settings.saveConfig();
        }
    }

    public static void setWorldSpawn(Player player) {
        if (authorized(player)) {
            Location loc = player.getLocation();
            player.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            player.sendMessage(unauthorized);
        }
    }

    public static void tellWorldName(Player player) {
        if (authorized(player)) {
            String worldName = player.getWorld().getName();
            player.sendMessage(ChatColor.AQUA + "Internal World Name: " + worldName);
        } else {
            player.sendMessage(unauthorized);
        }
    }
}
