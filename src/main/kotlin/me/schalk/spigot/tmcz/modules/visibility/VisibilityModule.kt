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
import me.schalk.spigot.lib.util.isSingleWorld
import me.schalk.spigot.tmcz.data.GameData
import me.schalk.spigot.lib.util.isAllowed as isWorldAllowed
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

abstract class VisibilityModule : Listener {

    companion object {
        const val MODULE        = "visibility-system"
        const val ENABLED       = ".enabled"
        const val IN_GAME_ONLY  = ".only-in-game"
        const val SERVER_WIDE   = ".server-wide"
        const val TICKS         = ".ticks"

        fun initialize(plugin: JavaPlugin) {
            if (isSingleWorld(getSettings())) {
                if (getSettings().getConfig().getBoolean(MODULE + ENABLED)) {
                    VisibilitySchedule(plugin)
                    VisibilityListener(plugin);
                    plugin.logger.info("Visibility System Online!");
                }
            } else {
                plugin.logger.info("Multi-world enabled, Visibility System not supported on Multi-world.... yet")
            }
        }

        fun isAllowed(entity: Entity): Boolean {
            val worldAllowed: Boolean = isWorldAllowed(entity.world, getSettings())
            if (entity.type != EntityType.PLAYER) return false
            if (getSettings().getConfig().getBoolean(MODULE + SERVER_WIDE)) return true
            return if (getSettings().getConfig().getBoolean(MODULE + IN_GAME_ONLY))
                GameData.getPlayer(entity as Player).playing && worldAllowed else worldAllowed
        }
    }
}