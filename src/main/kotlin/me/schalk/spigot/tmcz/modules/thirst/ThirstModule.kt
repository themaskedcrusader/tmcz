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
import me.schalk.spigot.tmcz.data.GameData
import me.schalk.spigot.lib.util.isAllowed as isWorldAllowed
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

abstract class ThirstModule : Listener {

    companion object {
        // Settings for Thirst System
        const val MODULE        = "thirst-system"
        const val ENABLED       = "$MODULE.enabled"
        const val IN_GAME_ONLY  = "$MODULE.only-in-game"
        const val SERVER_WIDE   = "$MODULE.server-wide"
        const val TICKS         = "$MODULE.thirst-ticks"
        const val DAMAGE_TICKS  = "$MODULE.damage-ticks"
        const val DAMAGE_HIT    = "$MODULE.damage-hit"
        const val FULL          = "$MODULE.full"
        const val REFILL_WATER  = "$MODULE.refill-water"
        const val REFILL_POTION = "$MODULE.refill-potion"
        const val BREAK_CHANCE  = "$MODULE.break-chance"
        const val START         = "$MODULE.start"
        const val PARCH_1       = "$MODULE.parch-1"
        const val PARCH_2       = "$MODULE.parch-2"
        const val PARCH_3       = "$MODULE.parch-3"
        const val PARCH_4       = "$MODULE.parch-4"
        const val DEATH_LVL     = "$MODULE.death-lvl"
        const val CHECK_TICKS   = "$MODULE.check-ticks"

        // Messages for Thirst System
        const val PARCH_1_MSG   = "$MODULE.parch-1"
        const val PARCH_2_MSG   = "$MODULE.parch-2"
        const val PARCH_3_MSG   = "$MODULE.parch-3"
        const val PARCH_4_MSG   = "$MODULE.parch-4"
        const val DEATH_MESSAGE = "$MODULE.death"
        const val REFILL_MSG    = "$MODULE.refill"

        fun initialize(plugin: JavaPlugin) {
            if (getSettings().getConfig().getBoolean(ENABLED)) {
                ThirstListener(plugin)
                ThirstSchedule(plugin)
                plugin.logger.info("Thirst System Online!")
            }
        }

        fun isAllowed(entity: Entity): Boolean {
            val worldAllowed: Boolean = isWorldAllowed(entity.world, getSettings())
            if (entity.type != EntityType.PLAYER) return false
            if (getSettings().getConfig().getBoolean(SERVER_WIDE)) return true
            return if (getSettings().getConfig().getBoolean(IN_GAME_ONLY))
                GameData.getPlayer(entity as Player).playing && worldAllowed else worldAllowed
        }
    }
}