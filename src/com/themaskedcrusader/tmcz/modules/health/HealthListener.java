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

package com.themaskedcrusader.tmcz.modules.health;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealthListener implements Listener {

    public HealthListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Health.isAllowed(event.getPlayer())) {
            event.getPlayer().setHealth(Settings.getConfig().getInt(Health.START));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaximumHealth(EntityRegainHealthEvent event) {
        if (Settings.getConfig().getInt(Health.MAXIMUM) < 20 &&
                Health.isAllowed(event.getEntity())) {
            int max = Settings.getConfig().getInt(Health.MAXIMUM);
            Player player = (Player) event.getEntity();
            if (player.getHealth() + event.getAmount() > max) {
                player.setHealth(max);
                event.setCancelled(true);
            }
        }
    }

    public void foodHeals(FoodLevelChangeEvent event) {
        if (Health.isAllowed(event.getEntity()) && Settings.getConfig().getBoolean(Health.FOOD)) {
            PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 5, 2 );
            Player player = (Player) event.getEntity();
            player.addPotionEffect(regen);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void manageRegeneration(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.PLAYER && WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get())) {
            if ((!Settings.getConfig().getBoolean(Health.REGEN) &&
                    event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) ||
                    ((Player) entity).getHealth() >= Settings.getConfig().getInt(Health.MAXIMUM)) {
                event.setCancelled(true);
            }
        }
    }
}
