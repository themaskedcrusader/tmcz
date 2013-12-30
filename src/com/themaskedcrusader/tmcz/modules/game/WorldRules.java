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

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldRules implements Listener {

    public WorldRules(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void cancelAgroIfNotPlaying(EntityTargetLivingEntityEvent event) {
        if (event.getTarget().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getTarget();
            if (!PlayerUtil.isPlaying(player) && WorldUtils.isAllowed(player.getWorld(), Settings.get())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void cancelXpDropsOnGameWorld(EntityDeathEvent event) {
        if (WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get())) {
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void cancelDropsOnGameWorld(EntityDeathEvent event) {
        if (WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get())) {
            event.getDrops().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void cancelPlayerDamageIfNotPlaying(EntityDamageByEntityEvent event) {
        if (WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get())) {
            Entity damaged = event.getEntity();
            Entity damager = event.getDamager();
            if ((damaged.getType() == EntityType.PLAYER && !PlayerUtil.isPlaying((Player) damaged)) ||
                    (damager.getType() == EntityType.PLAYER && !PlayerUtil.isPlaying((Player) damager)) ) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void cancelPvpIfNotEnabled(EntityDamageByEntityEvent event) {
        if (WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get())) {
            Entity damaged = event.getEntity();
            Entity damager = event.getDamager();
            if (!Settings.getConfig().getBoolean(Game.PVP) && damaged.getType() == EntityType.PLAYER &&
                    damager.getType() == EntityType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockSpawningIfSettingIsSet(CreatureSpawnEvent event) {
        if (WorldUtils.isAllowed(event.getLocation().getWorld(),  Settings.get())) {
            if (event.getEntity().getType() == EntityType.GIANT) return;  // explicitly allow giant spawns
            if (Settings.getConfig().getBoolean(Game.ONLY_ZOMBIES) && event.getEntityType() != EntityType.ZOMBIE) {
                event.setCancelled(true);
            }
        }
    }
}
