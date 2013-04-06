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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSaveListener implements Listener {
    JavaPlugin plugin;

    public AutoSaveListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void playerLogin(PlayerLoginEvent event) {
        AutoSave.loadPlayer(event.getPlayer(), plugin);
    }

    @EventHandler
    public void playerWorldChange(PlayerChangedWorldEvent event) {
        AutoSave.loadPlayer(event.getPlayer(), plugin);
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        AutoSave.loadPlayer(event.getPlayer(), plugin);
    }

    @EventHandler
    public void playerLogout(PlayerQuitEvent event) {
        AutoSave.saveToDisk(event.getPlayer(), plugin);
    }

    @EventHandler
    public void playerDie(PlayerDeathEvent event){
        AutoSave.saveToDisk(event.getEntity(), plugin);
    }
}
