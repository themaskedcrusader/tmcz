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
    public static final String RETURN  = SYSTEM + ".return-to-spawn";

    // To suppress instanciability
    private AutoSave() {}

    public static void initialize(JavaPlugin plugin) {
        new AutoSaveListener(plugin);
        if (Settings.getConfig().getBoolean(ENABLED)) {
            new AutoSaveSchedule(plugin);
            plugin.getLogger().info("Autosave System Online");
        }
    }

    public static boolean loadFromDisk(Player player, JavaPlugin plugin) {
        File playerFile = new File(getFileName(player, plugin));
        if (playerFile.exists()) {
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            PlayerBean pb = new PlayerBean(player.getDisplayName());
            pb.setBleeding(playerConfig.getBoolean(SaveConfig.BLEEDING));
            pb.setInfected(playerConfig.getBoolean(SaveConfig.INFECTED));
            pb.setPlayerKills(playerConfig.getInt(SaveConfig.PLAYER_KILLS));
            pb.setZombieKills(playerConfig.getInt(SaveConfig.ZOMBIE_KILLS));
            pb.setPlayerHeals(playerConfig.getInt(SaveConfig.PLAYER_HEALS));
            PlayerUtil.putPlayer(pb);
            return true;
        }
        return false;
    }

    public static void saveToDisk(Player player, JavaPlugin plugin) {
        PlayerBean pb = PlayerUtil.getPlayer(player.getDisplayName());
        if (pb != null) {
            File playerConfigFile = new File(getFileName(player, plugin));
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
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

    private static void removeFromDisk(Player player, JavaPlugin plugin) {
        File playerFile = new File(getFileName(player, plugin));
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }

    public static void reloadAllPlayers(JavaPlugin plugin) {
        World world = WorldUtils.getAuthorizedWorld(plugin, Settings.get());
        if (world != null) {
            for (Player player : world.getPlayers()) {
                loadFromDisk(player, plugin);
            }
        }
    }

    public static void saveAllPlayers(JavaPlugin plugin) {
        World world = WorldUtils.getAuthorizedWorld(plugin, Settings.get());
        for (Player player : world.getPlayers()) {
            saveToDisk(player, plugin);
        }
    }

    public static void loadPlayer(Player player, JavaPlugin plugin) {
        AutoSave.loadFromDisk(player, plugin);
        if (PlayerUtil.getPlayer(player) == null) {
            PlayerUtil.initPlayer(player);
            AutoSave.saveToDisk(player, plugin);
        }
    }
}
