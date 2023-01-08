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

import me.schalk.spigot.lib.config.getMessages
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
        val ENABLED       = getSettings().getConfig().getBoolean(MODULE + ".enabled")
        val IN_GAME_ONLY  = getSettings().getConfig().getBoolean(MODULE + ".only-in-game")
        val SERVER_WIDE   = getSettings().getConfig().getBoolean(MODULE + ".server-wide")
        val TICKS         = getSettings().getConfig().getLong(MODULE + ".thirst-ticks")
        val DAMAGE_TICKS  = getSettings().getConfig().getLong(MODULE + ".damage-ticks")
        val DAMAGE_HIT    = getSettings().getConfig().getDouble(MODULE + ".damage-hit")
        val FULL          = getSettings().getConfig().getInt(MODULE + ".full")
        val REFILL_WATER  = getSettings().getConfig().getInt(MODULE + ".refill-water")
        val REFILL_POTION = getSettings().getConfig().getInt(MODULE + ".refill-potion")
        val BREAK_CHANCE  = getSettings().getConfig().getInt(MODULE + ".break-chance")
        val START         = getSettings().getConfig().getInt(MODULE + ".start")
        val PARCH_1       = getSettings().getConfig().getInt(MODULE + ".parch-1")
        val PARCH_2       = getSettings().getConfig().getInt(MODULE + ".parch-2")
        val PARCH_3       = getSettings().getConfig().getInt(MODULE + ".parch-3")
        val PARCH_4       = getSettings().getConfig().getInt(MODULE + ".parch-4")

        // Messages for Thirst System
        val PARCH_1_MSG   = getMessages().getConfig().getString(MODULE + ".parch-1")
        val PARCH_2_MSG   = getMessages().getConfig().getString(MODULE + ".parch-2")
        val PARCH_3_MSG   = getMessages().getConfig().getString(MODULE + ".parch-3")
        val PARCH_4_MSG   = getMessages().getConfig().getString(MODULE + ".parch-4")
        val DEATH_MESSAGE = getMessages().getConfig().getString(MODULE + ".death")
        val REFILL_MESSAGE= getMessages().getConfig().getString(MODULE + ".refill")

        fun initialize(plugin: JavaPlugin) {
            if (ENABLED) {
                ThirstListener(plugin)
                ThirstSchedule(plugin)
                plugin.logger.info("Thirst System Online!")
            }
        }

        fun isAllowed(entity: Entity): Boolean {
            val worldAllowed: Boolean = isWorldAllowed(entity.world, getSettings())
            if (entity.type != EntityType.PLAYER) return false
            if (SERVER_WIDE) return true
            return if (IN_GAME_ONLY) GameData.getPlayer(entity as Player).playing && worldAllowed else worldAllowed
        }
    }
}