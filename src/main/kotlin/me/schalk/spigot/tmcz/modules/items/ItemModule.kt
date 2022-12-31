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

package me.schalk.spigot.tmcz.modules.items

import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.tmcz.data.GameData
import me.schalk.spigot.lib.util.isAllowed as isWorldAllowed
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

abstract class ItemModule : Listener {

    companion object {
        const val MODULE        = "item-system"
        const val ENABLED       = ".enabled"
        const val IN_GAME       = ".only-in-game"
        const val SERVER_WIDE   = ".server-wide"
        const val RESPAWN       = ".respawn"
        const val R_SECONDS     = ".respawn-seconds"
        const val TOOL_ID       = ".tool"


        fun initialize(plugin: JavaPlugin) {
            if (getSettings().getConfig().getBoolean(MODULE + ENABLED)) {
                BlockListener(plugin)
                CobwebListener(plugin)
                ItemListener(plugin)
                GrenadeListener(plugin)
                MelonListener(plugin)
                MushroomListener(plugin)
                plugin.logger.info("Registered custom item interactions!")
            }
        }

        fun isAllowed(player: Player, serverWide: String, inGame: String): Boolean {
            val worldAllowed: Boolean = isWorldAllowed(player.world, getSettings())
            if (getSettings().getConfig().getBoolean(serverWide)) return true
            return if (getSettings().getConfig().getBoolean(inGame))
                GameData.getPlayer(player).playing && worldAllowed else worldAllowed
        }
    }
}