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

package com.themaskedcrusader.tmcz.modules.bleed;

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.data.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BleedListener implements Listener {
    Random random = new Random();

    public BleedListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void bleedPlayer(EntityDamageEvent event) {
        if (Bleed.isAllowed(event.getEntity())) {
            Player player = (Player) event.getEntity();
            int chance = Settings.getConfig().getInt(Bleed.CHANCE);
            if (chance > random.getChance() && !PlayerUtil.isStatusSet(player, Status.BLEEDING)) {

                if (
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                        event.getCause() == EntityDamageEvent.DamageCause.FALL ||
                        event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                        event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                   ) {

                    int seconds = Settings.getConfig().getInt(Bleed.SECONDS) * 20;
                    PotionEffect bleed = new PotionEffect(PotionEffectType.getById(15), seconds, 1);
                    player.sendMessage(ChatColor.RED + Messages.getConfig().getString(Bleed.HIT_MSG));
                    player.addPotionEffect(bleed);
                    PlayerUtil.setStatus(player, Status.BLEEDING, true);
                }
            }
        }
    }

    @EventHandler
    public void stopBleedWithBandage(PlayerInteractEvent event) {
        if (Bleed.isAllowed(event.getPlayer())) {
            Player player = event.getPlayer();
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                    player.getItemInHand().getType() == Material.PAPER) {
                useBandage(player);
            }
        }
    }

    private void useBandage(Player player) {
        if (PlayerUtil.isStatusSet(player, Status.BLEEDING)) {
            PlayerUtil.setStatus(player, Status.BLEEDING, false);

            PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 4, 2);
            player.addPotionEffect(regen);

            if (player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.setItemInHand(null);
            }

            player.sendMessage(ChatColor.GREEN + Messages.getConfig().getString(Bleed.STOP_1_MSG));
        }
    }
}

