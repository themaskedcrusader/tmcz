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
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

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
            plugin.server.onlinePlayers.forEach{ player ->
                if (isAllowed(player) && GameData.getPlayer(player).bleeding) {
                    plugin.server.scheduler.runTaskLater(plugin, Runnable {
                        player.damage(getSettings().getConfig().getDouble(MODULE + DAMAGE))
                        player.spawnParticle(Particle.DAMAGE_INDICATOR, player.location, getSettings().getConfig().getInt(MODULE + PARTICLES),  0.2, 0.2, 0.2, 0.01)
                                                                          }, 10)
                    player.sendMessage(ChatColor.RED.toString() + getMessages().getConfig().getString(MODULE + MESSAGE))
                }
            }
        }
    }
}