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

package com.themaskedcrusader.tmcz.modules;

import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.modules.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultListener implements Listener {
    private static final String RECORD_ZOMBIE_KILLS = "world.record-zombie-kills";

    public DefaultListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void recordZombieKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (isAllowed(entity.getWorld()) && entity.getType() == EntityType.ZOMBIE &&
                entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) entity.getLastDamageCause();
            if (damage.getDamager().getType() == EntityType.PLAYER) {
                Player player = (Player) damage.getDamager();
                addKill(player);

            } else if (damage.getDamager().getType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) damage.getEntity();
                Entity player = arrow.getShooter();
                if (player instanceof Player) {
                    addKill((Player) player);
                }
            }
        }
    }

    private void addKill(Player player) {
        PlayerUtil.addZombieKill(player);
        player.sendMessage(ChatColor.YELLOW +
                Messages.getConfig().getString(Game.ZOMBIE_KILLS) + " " +
                PlayerUtil.getZombieKills(player));
    }

    @EventHandler
    public void removeFromGameOnDeath(PlayerDeathEvent event) {
        PlayerUtil.removePlayer(event.getEntity());
    }

    private boolean isAllowed(World world) {
        boolean worldAllowed = WorldUtils.isAllowed(world, Settings.get());
        return (!Settings.getConfig().getBoolean(WorldUtils.SINGLE_WORLD)) || worldAllowed;
    }
}
