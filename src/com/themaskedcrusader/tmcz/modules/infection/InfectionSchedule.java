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

import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.data.Status;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfectionSchedule {
    private static JavaPlugin plugin;

    public InfectionSchedule(JavaPlugin plugin) {
        InfectionSchedule.plugin = plugin;
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                InfectionSchedule.infectThePlayer();
            }
        }, 20L, (Settings.getConfig().getLong(Infection.TICKS)));
    }

    public static void infectThePlayer() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            if (Infection.isAllowed(player) && PlayerUtil.isStatusSet(player, Status.INFECTED)) {
                int seconds = Settings.getConfig().getInt(Infection.DURATION) * 20;
                int damage = Settings.getConfig().getInt(Infection.DAMAGE) * 13;
                PotionEffect infectionEffect = new PotionEffect(PotionEffectType.getById(9), seconds, 1);
                PotionEffect infectionDamage = new PotionEffect(PotionEffectType.getById(19), damage, 1);
                player.addPotionEffect(infectionEffect);
                player.addPotionEffect(infectionDamage);
                if (Settings.getConfig().getBoolean(Infection.HURL)) {
                    PotionEffect infectionHurl = new PotionEffect(PotionEffectType.getById(17), seconds, 1);
                    player.addPotionEffect(infectionHurl);
                }
                player.sendMessage(ChatColor.RED + Messages.getConfig().getString(Infection.MESSAGE));
            }
        }
    }
}
