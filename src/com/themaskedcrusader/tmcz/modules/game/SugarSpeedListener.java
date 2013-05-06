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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SugarSpeedListener implements Listener {
    JavaPlugin plugin;

    public SugarSpeedListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void eatSugarForSpeed(PlayerInteractEvent event) {
        if (WorldUtils.isAllowed(event.getPlayer().getWorld(), Settings.get()) &&
                event.getPlayer().getItemInHand().getType() == Material.SUGAR) {
            Player player = event.getPlayer();
            ItemStack sugar = player.getItemInHand();
            sugar.setAmount(sugar.getAmount() - 1);
            int duration = Settings.getConfig().getInt(Game.SUGAR_SPEED + Game.DURATION);
            int multiplier = Settings.getConfig().getInt(Game.SUGAR_SPEED + Game.MULTIPLIER);
            PotionEffect sugarSpeed = new PotionEffect(PotionEffectType.SPEED, duration * 20, multiplier - 1);
            player.addPotionEffect(sugarSpeed);
        }
    }
}
