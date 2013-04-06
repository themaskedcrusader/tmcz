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

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodListener implements Listener {

    public FoodListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void foodGivesHealth(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (Health.isAllowed(event.getEntity()) && event.getFoodLevel() > player.getFoodLevel()) {
            int foodLevel = player.getFoodLevel();
            int duration = 1;
            if (foodLevel <  18) { duration += 2; }
            if (foodLevel <= 14) { duration += 2; }
            if (foodLevel <=  9) { duration += 2; }
            if (foodLevel <   4) { duration += 2; }

            PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, duration * 20, 0);
            if (event.getFoodLevel() > 19 && event.getEntity().getType() == EntityType.PLAYER) {
                ((Player) event.getEntity()).setFoodLevel(19);
                event.setCancelled(true);
            }
            event.getEntity().addPotionEffect(regen);
        }
    }
}
