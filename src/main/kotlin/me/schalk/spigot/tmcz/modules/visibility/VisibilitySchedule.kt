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

package me.schalk.spigot.tmcz.modules.visibility

import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.lib.util.isPlayerFalling
import me.schalk.spigot.lib.util.isPlayerMoving
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class VisibilitySchedule(plugin: JavaPlugin) : VisibilityModule() {

    init{
        VisibilitySchedule.plugin = plugin
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin,
            { calculateVisibility() }, 20L, getSettings().getConfig().getLong(MODULE + TICKS)
        )
    }

    companion object {
        private lateinit var plugin: JavaPlugin
        private var playerMovement = mutableMapOf<String, Location>()
        private const val _IN_GAME_ONLY = MODULE + IN_GAME_ONLY

        fun calculateVisibility() {
            val players = plugin.server.onlinePlayers
            players.forEach { player ->
                if (isAllowed(player)) {
                    val visibility = calculateVisibility(player)
                    player.exp = visibility / 7
                    playerMovement[player.displayName] = player.location
                }
            }
        }

        private fun calculateVisibility(player: Player): Float {
            var visibility = 2f
            val loc = playerMovement[player.displayName]
            if ((!getSettings().getConfig().getBoolean(_IN_GAME_ONLY) || GameData.getPlayer(player).playing) && loc != null) {
                if (isPlayerMoving(player, loc)) {
                    visibility += 1f
                }
                if (isPlayerFalling(player, loc)) {
                    visibility += 1f
                }
                if (player.isSneaking) {
                    visibility -= 1f
                }
                if (player.isSprinting) {
                    visibility += 2f
                }
                if (player.world.isThundering) {
                    visibility -= 0.5f
                }
                return visibility
            }
            return 0f
        }
    }

}