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

package com.themaskedcrusader.tmcz.modules.thirst;

import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ThirstListener implements Listener {
    private JavaPlugin plugin;

    public ThirstListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void drinkWater(PlayerInteractEvent event) {
        if (Thirst.isAllowed(event.getPlayer())) {
            Player player = event.getPlayer();
            ItemStack item = player.getItemInHand();
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                    item.getType() == Material.POTION && item.getData().getData() == (byte) 0) {
                int heldSlot = player.getInventory().getHeldItemSlot();
                useWaterBottle(player, heldSlot);
            }
        }
    }

    @EventHandler
    public void setThirstLevelOnSpawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (Thirst.isAllowed(player))
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                player.setLevel(Settings.getConfig().getInt(Thirst.START));
            }
        }, 5L);
    }

    @EventHandler
    public void preventExpDropOnDeath(EntityDeathEvent event) {
        if (Thirst.isAllowed(event.getEntity())) {
            event.setDroppedExp(0);
        }
    }

    public void useWaterBottle(final Player player, final int heldSlot) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                try {
                    if (player.getInventory().getItem(heldSlot).getType() == Material.GLASS_BOTTLE) {
                        int newLevel = player.getLevel() + Settings.getConfig().getInt(Thirst.REFILL);
                        player.setLevel(Math.min(newLevel, Settings.getConfig().getInt(Thirst.FULL)));
                        player.sendMessage(ChatColor.AQUA + Messages.getConfig().getString(Thirst.REFILL_MSG));
                    }
                } catch (Exception e) {
                    plugin.getServer().getLogger().log(Level.INFO, "Player couldn't drink...", e.getMessage());
                }
            }
        }, Settings.getConfig().getLong(Thirst.CHECK_TICKS));
    }
}