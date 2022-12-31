/*
 * Copyright (c) 2013 - 2023 Christopher Schalk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.schalk.spigot.tmcz.modules.bleed

import me.schalk.spigot.lib.config.getMessages
import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class BleedSchedule(plugin: JavaPlugin) : BleedModule() {

    init{
        BleedSchedule.plugin = plugin
        plugin.server.scheduler.runTaskTimerAsynchronously(
            plugin,
            Runnable { bleedThePlayer() }, 20L, getSettings().getConfig().getLong(MODULE + TICKS)
        )
    }

    companion object {
        lateinit var plugin: JavaPlugin

        fun bleedThePlayer() {
            val players: Collection<Player> = plugin.server.onlinePlayers
            players.forEach{ player ->
                if (isAllowed(player) && GameData.getPlayer(player).bleeding) {
                    val seconds: Int = getSettings().getConfig().getInt(MODULE + SECONDS) * 20
                    val damage: Int = getSettings().getConfig().getInt(MODULE + DAMAGE) * 13
                    val bleedPotion = PotionEffect(PotionEffectType.BLINDNESS, seconds, 1)
                    val bleedDamage = PotionEffect(PotionEffectType.POISON, damage, 1)
                    player.addPotionEffect(bleedPotion)
                    player.addPotionEffect(bleedDamage)
                    player.sendMessage(ChatColor.RED.toString() + getMessages().getConfig().getString(MODULE + MESSAGE))
                }
            }
        }
    }
}