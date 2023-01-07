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
import me.schalk.spigot.tmcz.data.BlockBean
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class CobwebListener(plugin: JavaPlugin) : ItemModule() {

    companion object {
        private const val COBWEB            = MODULE + ".cobweb"
        private const val _ENABLED          = COBWEB + ENABLED
        private const val _IN_GAME          = COBWEB + IN_GAME
        private const val _SERVER_WIDE      = COBWEB + SERVER_WIDE
        private const val _ALLOWED_TOOL     = COBWEB + TOOL_ID
        private const val _ALLOW_DROPS      = COBWEB + ".drop"
        private const val _ALLOW_RESPAWN    = COBWEB + RESPAWN
        private const val _RESPAWN_SECONDS  = COBWEB + R_SECONDS

        fun cleanUp() {
            BlockListener.brokenBlocks.forEach { cobweb ->
                val location = cobweb.key
                val block = cobweb.value
                val now = Date().time
                if (block.material == Material.COBWEB
                    && (now - block.date.time) > getSettings().getConfig().getLong(_RESPAWN_SECONDS).times(1000)) {
                    location.world!!.getBlockAt(location).type = Material.COBWEB
                    BlockListener.brokenBlocks.remove(location)
                }
            }
        }
    }

    init {
        if (getSettings().getConfig().getBoolean(_ENABLED)) {
            plugin.server.pluginManager.registerEvents(this, plugin)
            if (getSettings().getConfig().getBoolean(_ALLOW_RESPAWN)) {
                plugin.server.scheduler.scheduleSyncRepeatingTask(plugin,
                    { cleanUp() }, 15L, getSettings().getConfig().getLong(_RESPAWN_SECONDS) * 20L
                )
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun breakCobweb(event: BlockBreakEvent) {
        if (allowCobweb(event.player, event.block)) {
            val block = event.block
            BlockListener.brokenBlocks[block.location] = BlockBean(block)
            block.type = Material.AIR
            if (getSettings().getConfig().getBoolean(_ALLOW_DROPS)) {
                block.world.dropItem(
                    block.location, ItemStack(Material.COBWEB, 1)
                )
            }
        }
    }

    private fun allowCobweb(player: Player, block: Block): Boolean {
        return isAllowed(player, _SERVER_WIDE, _IN_GAME)
                && block.type == Material.COBWEB
                && player.inventory.itemInMainHand.type == Material.getMaterial(getSettings().getConfig().getString(_ALLOWED_TOOL)!!.uppercase())
    }
}