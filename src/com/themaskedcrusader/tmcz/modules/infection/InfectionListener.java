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

package com.themaskedcrusader.tmcz.modules.infection;

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.data.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfectionListener implements Listener {
    private JavaPlugin plugin;
    Random random = new Random();

    public InfectionListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void infectPlayer(EntityDamageByEntityEvent event) {
        if (Infection.isAllowed(event.getEntity())) {
            Entity damager = event.getDamager();
            Entity damaged = event.getEntity();
            if (damaged.getType() == EntityType.PLAYER &&
                    damager.getType() == EntityType.ZOMBIE &&
                    !PlayerUtil.isStatusSet((Player) damaged, Status.INFECTED)) {
                int chance = Settings.getConfig().getInt(Infection.CHANCE);
                if (chance > random.getChance()) {
                    PlayerUtil.setStatus((Player) damaged, Status.INFECTED, true);
                    PotionEffect infection = new PotionEffect(PotionEffectType.CONFUSION, 15, 1);
                    ((Player) damaged).sendMessage(ChatColor.RED + Messages.getConfig().getString(Infection.HIT_MSG));
                    ((Player) damaged).addPotionEffect(infection);
                }
            }
        }
    }

    @EventHandler
    public void drinkMilk(PlayerInteractEvent event) {
        if (Infection.isAllowed(event.getPlayer())) {
            Player player = event.getPlayer();
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                    player.getItemInHand().getType() == Material.MILK_BUCKET) {
                int heldSlot = player.getInventory().getHeldItemSlot();
                useMilkBukkit(player, heldSlot);
            }
        }
    }

    public void useMilkBukkit(final Player player, final int heldSlot) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Player toModify = plugin.getServer().getPlayer(player.getName());
                ItemStack item = toModify.getInventory().getItem(heldSlot);
                if (item.getType() == Material.BUCKET) {
                    toModify.getInventory().setItem(heldSlot, new ItemStack(Material.AIR));
                    PlayerUtil.setStatus(toModify, Status.INFECTED, false);
                    toModify.sendMessage(ChatColor.AQUA + Messages.getConfig().getString(Infection.CURE_1_MSG));
                }
            }
        }, Settings.getConfig().getLong(Infection.CHECK_TICKS));
    }
}
