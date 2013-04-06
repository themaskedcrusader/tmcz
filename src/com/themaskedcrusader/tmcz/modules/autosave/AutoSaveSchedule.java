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

package com.themaskedcrusader.tmcz.modules.autosave;

import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSaveSchedule {
    private static JavaPlugin plugin;

    public AutoSaveSchedule(JavaPlugin plugin) {
        AutoSaveSchedule.plugin = plugin;
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                AutoSaveSchedule.savePlayers();
            }
        }, 30L, Settings.getConfig().getLong(AutoSave.MINUTES) * 1200L);
    }


    public static void savePlayers() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            AutoSave.saveToDisk(player, plugin);
        }
    }
}
