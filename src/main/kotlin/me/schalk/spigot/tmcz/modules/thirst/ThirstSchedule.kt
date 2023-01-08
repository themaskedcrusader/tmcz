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

package me.schalk.spigot.tmcz.modules.thirst

import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.lib.serializer.abovePlayerHead
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.ChatColor
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ThirstSchedule(plugin: JavaPlugin) : ThirstModule() {
    
    init{
        ThirstSchedule.plugin = plugin
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { calculateThirst() }, 20L, TICKS )
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { calculateThirstDamage() }, 20L, DAMAGE_TICKS)
    }

    companion object {
        private lateinit var plugin: JavaPlugin

        fun calculateThirst() {
            plugin.server.onlinePlayers.forEach{ player ->
                if (isAllowed(player) && player.level > 0) {
                    player.level = player.level - 1
                    sendPlayerThirstMessage(player)
                    plugin.server.scheduler.runTaskLater(plugin, Runnable {
                        val location = abovePlayerHead(player.location)
                        player.spawnParticle(Particle.BUBBLE_POP, location.x, location.y, location.z, 1, 0.2, 0.2, 0.2, 0.01)
                    }, 5)
                }
            }
        }

        fun calculateThirstDamage() {
            plugin.server.onlinePlayers.forEach{ player ->
                if (isAllowed(player) && player.level == 0) {
                    GameData.getPlayer(player).thirsty = true
                    if (!player.isOp || !getSettings().getConfig().getBoolean("world.op-is-god")) { // TODO move to world config
                        player.damage(DAMAGE_HIT)
                        val location = abovePlayerHead(player.location)
                        player.spawnParticle(Particle.BUBBLE_POP, location.x, location.y, location.z, 3, 0.3, 0.3, 0.3, 0.01)
                    }
                    if (player.health <= 4.0) {
                        if (!player.hasPotionEffect(PotionEffectType.CONFUSION)) {
                            player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION,500, 255, false, false, false))
                        }
                    }
                }
            }
        }

        private fun sendPlayerThirstMessage(player: Player) {
            val yellow = ChatColor.YELLOW.toString()
            when (player.level) {
                PARCH_1 -> { player.sendMessage(yellow + PARCH_1_MSG) }
                PARCH_2 -> { player.sendMessage(yellow + PARCH_2_MSG) }
                PARCH_3 -> { player.sendMessage(yellow + PARCH_3_MSG) }
                PARCH_4 -> { player.sendMessage(yellow + PARCH_4_MSG) }
            }
        }
    }
}