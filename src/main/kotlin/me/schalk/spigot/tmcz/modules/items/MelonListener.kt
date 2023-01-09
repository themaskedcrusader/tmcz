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
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class MelonListener(plugin: JavaPlugin) : ItemModule() {

    companion object {
        private const val MELON     = MODULE + ".melon"
        private val IS_ENABLED      = getSettings().getConfig().getBoolean(MELON + ENABLED)
        private val IS_IN_GAME_ONLY = getSettings().getConfig().getBoolean(MELON + IN_GAME_ONLY)
        private val IS_SERVER_WIDE  = getSettings().getConfig().getBoolean(MELON + SERVER_WIDE)
        private val ALLOWED_TOOL    = getSettings().getConfig().getString(MELON + TOOL_ID)
    }

    init {
        if (IS_ENABLED) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun harvestMelon(event: BlockBreakEvent) {
        if (allowMelon(event.player, event.block)) {
            // TODO, un-cancel event if it has been cancelled by Block Worker
            // TODO: Find out how to identify whether this event fired first so BlockWorker doesn't cancel this event
            println("Let the melons hit the floor")
        }
    }

    private fun allowMelon(player: Player, block: Block): Boolean {
        return isAllowed(player, IS_SERVER_WIDE, IS_IN_GAME_ONLY)
                && block.type == Material.MELON
                && player.inventory.itemInMainHand.type == Material.getMaterial(ALLOWED_TOOL.toString().uppercase())
    }
}