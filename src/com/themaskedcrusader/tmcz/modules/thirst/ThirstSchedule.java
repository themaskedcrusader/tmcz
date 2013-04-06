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

package com.themaskedcrusader.tmcz.modules.thirst;

import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ThirstSchedule {
    private static JavaPlugin plugin;

    public ThirstSchedule(JavaPlugin plugin) {
        ThirstSchedule.plugin = plugin;

        // Schedule to calculate thirst
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                ThirstSchedule.calculateThirst();
            }
        }, 20L, Settings.getConfig().getLong(Thirst.TICKS));

        // Schedule to damage due to thirst
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                ThirstSchedule.calculateThirstDamage();
            }
        }, 20L, Settings.getConfig().getLong(Thirst.DAMAGE_TICKS));
    }


    public static void calculateThirst() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            if (Thirst.isAllowed(player) && player.getLevel() > 0) {
                player.setLevel(player.getLevel() - 1);
                sendPlayerThirstMessage(player);
            }
        }
    }

    public static void calculateThirstDamage() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            if (Thirst.isAllowed(player) && player.getLevel() == 0) {
                if (!player.isOp() || !Settings.getConfig().getBoolean("world.op-is-god")) {
                    player.damage(Settings.getConfig().getInt(Thirst.DAMAGE_HIT));
                }
                if (player.getHealth() == Settings.getConfig().getInt(Thirst.DEATH_LVL)) {
                    player.sendMessage(ChatColor.RED + Messages.getConfig().getString(Thirst.DEATH_MSG));
                }
            }
        }
    }

    private static void sendPlayerThirstMessage(Player player) {
        String message = "" + ChatColor.YELLOW;
        if (player.getLevel() == Settings.getConfig().getInt(Thirst.PARCH_1)) {
            player.sendMessage(message + Messages.getConfig().getString(Thirst.PARCH_1_MSG));
        } else if (player.getLevel() == Settings.getConfig().getInt(Thirst.PARCH_2)) {
            player.sendMessage(message + Messages.getConfig().getString(Thirst.PARCH_2_MSG));
        } else if (player.getLevel() == Settings.getConfig().getInt(Thirst.PARCH_3)) {
            player.sendMessage(message + Messages.getConfig().getString(Thirst.PARCH_3_MSG));
        } else if (player.getLevel() == Settings.getConfig().getInt(Thirst.PARCH_4)) {
            player.sendMessage(message + Messages.getConfig().getString(Thirst.PARCH_4_MSG));
        }
    }
}
