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

import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.data.Status;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BleedSchedule {
    private static JavaPlugin plugin;

    public BleedSchedule(JavaPlugin plugin) {
        BleedSchedule.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            public void run() {
                BleedSchedule.bleedThePlayer();
            }
        }, 20L, (Settings.getConfig().getLong(Bleed.TICKS)));
    }

    @SuppressWarnings("unchecked")
    public static void bleedThePlayer() {
        Player[]  players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            if (Bleed.isAllowed(player) && PlayerUtil.isStatusSet(player, Status.BLEEDING)) {
                int seconds = Settings.getConfig().getInt(Bleed.SECONDS) * 20;
                int damage = Settings.getConfig().getInt(Bleed.DAMAGE) * 13;
                PotionEffect bleedPotion = new PotionEffect(PotionEffectType.getById(15), seconds, 1);
                PotionEffect bleedDamage = new PotionEffect(PotionEffectType.getById(19), damage, 1);
                player.addPotionEffect(bleedPotion);
                player.addPotionEffect(bleedDamage);
                player.sendMessage(ChatColor.RED + Messages.getConfig().getString(Bleed.MESSAGE));
            }
        }
    }
}
