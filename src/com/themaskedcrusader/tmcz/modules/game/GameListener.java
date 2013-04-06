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

package com.themaskedcrusader.tmcz.modules.game;

import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.modules.autosave.AutoSave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GameListener implements Listener {
    private JavaPlugin plugin;

    public GameListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (WorldUtils.isAllowed(event.getPlayer().getWorld(), Settings.get())) {
            Player player = event.getPlayer();
            player.sendMessage(Messages.getConfig().getString(Game.MOTD));
            if (!AutoSave.loadFromDisk(player, plugin) && Settings.getConfig().getBoolean(Game.RETURN_TO_SPAWN)) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (WorldUtils.isAllowed(event.getPlayer().getWorld(), Settings.get())) {
            Player player = event.getPlayer();
            AutoSave.saveToDisk(player, plugin);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void recordPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null && PlayerUtil.isPlaying(player) && PlayerUtil.isPlaying(killer) &&
                WorldUtils.isAllowed(killer.getLocation().getWorld(), Settings.get())) {
            PlayerUtil.addPlayerKill(killer, Settings.get());
        }
    }

}
