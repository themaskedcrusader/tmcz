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

import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.Material
import me.schalk.spigot.lib.util.isAllowed as isWorldAllowed
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

abstract class BleedModule : Listener {

    companion object {
        // constants for the Bleed System
        const val MODULE            = "bleed-system"
        const val ENABLED           = ".enabled"
        const val IN_GAME           = ".only-in-game"
        const val SERVER_WIDE       = ".server-wide"
        const val PARTICLES         = ".particles"
        const val CHANCE            = ".chance"
        const val DAMAGE            = ".damage"
        const val TICKS             = ".ticks"
        const val APPROVED_ITEM     = ".healer-item"
        lateinit var BANDAGE        : Material

        // message keys for Bleed System
        const val MESSAGE           = ".message"
        const val HIT_MSG           = ".hit"
        const val SELF_STOP         = ".stop-own"
        const val DEATH_MESSAGE     = ".death-message"
        const val HEALER_MESSAGE    = ".healer-stop"
        const val HEALED_MESSAGE    = ".healed-stop"

        fun initialize(plugin: JavaPlugin) {
            if (getSettings().getConfig().getBoolean(MODULE + ENABLED)) {
                val configuredBandage = Material.getMaterial(getSettings().getConfig().getString(MODULE + APPROVED_ITEM)!!.uppercase())
                if (configuredBandage == null) {
                    plugin.logger.log(Level.SEVERE, "Approved bandage not a valid Minecraft Material")
                } else {
                    BANDAGE = configuredBandage
                }
                BleedListener(plugin)
                BleedSchedule(plugin)
                plugin.logger.info("Bleed System Online!")
            }
        }

        fun isAllowed(entity: Entity): Boolean {
            if (entity.type != EntityType.PLAYER) return false
            if (getSettings().getConfig().getBoolean(MODULE + SERVER_WIDE)) return true
            val worldAllowed: Boolean = isWorldAllowed(entity.world, getSettings())
            if (getSettings().getConfig().getBoolean(MODULE + IN_GAME)) {
                if (GameData.getPlayer(entity as Player).playing) {
                    return worldAllowed
                }
            } else {
                return worldAllowed
            }
            return false
        }
    }
}