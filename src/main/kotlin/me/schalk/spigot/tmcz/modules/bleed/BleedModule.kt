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
        const val MODULE    = "bleed-system"
        val ENABLED         = getSettings().getConfig().getBoolean(MODULE + ".enabled")
        val IN_GAME         = getSettings().getConfig().getBoolean(MODULE + ".only-in-game")
        val SERVER_WIDE     = getSettings().getConfig().getBoolean(MODULE + ".server-wide")
        val PARTICLES       = getSettings().getConfig().getInt(MODULE + ".particles")
        val CHANCE_TO_BLEED = getSettings().getConfig().getInt(MODULE + ".chance")
        val DAMAGE          = getSettings().getConfig().getDouble(MODULE + ".damage")
        val TICKS           = getSettings().getConfig().getLong(MODULE + ".ticks")
        val APPROVED_ITEM   = getSettings().getConfig().getString( MODULE + ".healer-item")
        lateinit var BANDAGE: Material

        // message keys for Bleed System
        val MESSAGE         = getMessages().getConfig().getString(MODULE + ".message")
        val HIT_MSG         = getMessages().getConfig().getString(MODULE + ".hit")
        val SELF_STOP       = getMessages().getConfig().getString(MODULE + ".stop-own")
        val DEATH_MESSAGE   = getMessages().getConfig().getString(MODULE + ".death-message")
        val HEALER_MESSAGE  = getMessages().getConfig().getString(MODULE + ".healer-stop")
        val HEALED_MESSAGE  = getMessages().getConfig().getString(MODULE + ".healed-stop")

        fun initialize(plugin: JavaPlugin) {
            if (ENABLED) {
                val configuredBandage = Material.getMaterial(APPROVED_ITEM!!.uppercase())
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
            if (SERVER_WIDE) return true
            val worldAllowed = isWorldAllowed(entity.world, getSettings())
            return if (IN_GAME && GameData.getPlayer(entity as Player).playing) worldAllowed else worldAllowed
        }
    }
}