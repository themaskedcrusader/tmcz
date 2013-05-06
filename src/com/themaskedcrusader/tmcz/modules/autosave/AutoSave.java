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
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerBean;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class AutoSave {
    private static final String SYSTEM = "autosave-system";
    public static final String ENABLED = SYSTEM + ".enabled";
    public static final String MINUTES = SYSTEM + ".minutes";

    private AutoSave() {}

    public static void initialize(JavaPlugin plugin) {
        reloadAllPlayers(plugin);
        new AutoSaveListener(plugin);
        if (Settings.getConfig().getBoolean(ENABLED)) {
            new AutoSaveSchedule(plugin);
            plugin.getLogger().info("Autosave System Online");
        }
    }

    public static boolean loadFromDisk(Player player, JavaPlugin plugin) {
        File playerFile = new File(getFileName(player, plugin));
        if (playerFile.exists()) {
            return loadFromDisk(playerFile);
        } else {
            return false;
        }
    }

    private static boolean loadFromDisk(File file) {
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
        PlayerBean pb = new PlayerBean(playerConfig.getString(SaveConfig.PLAYER_NAME));
        pb.setBleeding(playerConfig.getBoolean(SaveConfig.BLEEDING));
        pb.setInfected(playerConfig.getBoolean(SaveConfig.INFECTED));
        pb.setPlayerKills(playerConfig.getInt(SaveConfig.PLAYER_KILLS));
        pb.setZombieKills(playerConfig.getInt(SaveConfig.ZOMBIE_KILLS));
        pb.setPlayerHeals(playerConfig.getInt(SaveConfig.PLAYER_HEALS));
        PlayerUtil.putPlayer(pb);
        return true;
    }

    public static void saveToDisk(Player player, JavaPlugin plugin) {
        PlayerBean pb = PlayerUtil.getPlayer(player.getDisplayName());
        if (pb != null) {
            File playerConfigFile = new File(getFileName(player, plugin));
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
            playerConfig.set(SaveConfig.PLAYER_NAME, player.getDisplayName());
            playerConfig.set(SaveConfig.BLEEDING, pb.isBleeding());
            playerConfig.set(SaveConfig.INFECTED, pb.isInfected());
            playerConfig.set(SaveConfig.ZOMBIE_KILLS, pb.getZombieKills());
            playerConfig.set(SaveConfig.PLAYER_KILLS, pb.getPlayerKills());
            playerConfig.set(SaveConfig.PLAYER_HEALS, pb.getPlayerHeals());
            try {
                playerConfig.save(playerConfigFile);
            } catch (IOException e) {
                plugin.getLogger().info(e.getMessage());
            }
        }
    }

    private static String getFileName(Player player, JavaPlugin plugin) {
        return plugin.getDataFolder() + "/players/" + player.getDisplayName() + ".yml";
    }

    public static void removeFromDisk(Player player, JavaPlugin plugin) {
        File playerFile = new File(getFileName(player, plugin));
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }

    public static void reloadAllPlayers(JavaPlugin plugin) {
        File directory = new File(plugin.getDataFolder() + "/players" );
        for (File file : directory.listFiles()) {
            loadFromDisk(file);
        }
    }

    public static void saveAllPlayers(JavaPlugin plugin) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            saveToDisk(player, plugin);
        }
    }

    public static boolean loadPlayer(Player player, JavaPlugin plugin) {
        if (PlayerUtil.getPlayer(player) != null) {
            AutoSave.loadFromDisk(player, plugin);
            return (PlayerUtil.getPlayer(player) != null);
        } else {
            plugin.getLogger().info(player.getDisplayName() + " already playing, no need to load save game data.");
            return true;
        }
    }
}
