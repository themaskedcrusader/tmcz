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
        private const val COBWEB      = MODULE + ".cobweb"
        private val IS_ENABLED        = getSettings().getConfig().getBoolean(COBWEB + ENABLED)
        private val IS_IN_GAME_ONLY   = getSettings().getConfig().getBoolean(COBWEB + IN_GAME_ONLY)
        private val IS_SERVER_WIDE    = getSettings().getConfig().getBoolean(COBWEB + SERVER_WIDE)
        private val ALLOWED_TOOL      = getSettings().getConfig().getString(COBWEB + TOOL_ID)
        private val ALLOW_DROPS       = getSettings().getConfig().getBoolean(COBWEB + ".drop")
        private val ALLOW_RESPAWN     = getSettings().getConfig().getBoolean(COBWEB + RESPAWN)
        private val RESPAWN_SECONDS   = getSettings().getConfig().getLong(COBWEB + R_SECONDS)

        fun cleanUp() {
            BlockListener.brokenBlocks.forEach { cobweb ->
                val location = cobweb.key
                val block = cobweb.value
                val now = Date().time
                if (block.material == Material.COBWEB
                    && (now - block.date.time) > RESPAWN_SECONDS.times(1000)) {
                    val world = location.world
                    if (world != null) {
                        world.getBlockAt(location).type = Material.COBWEB
                    }
                    BlockListener.brokenBlocks.remove(location)
                }
            }
        }
    }

    init {
        if (IS_ENABLED) {
            plugin.server.pluginManager.registerEvents(this, plugin)
            if (ALLOW_RESPAWN)
                plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { cleanUp() }, 15L, RESPAWN_SECONDS * 20L)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun breakCobweb(event: BlockBreakEvent) {
        if (allowCobweb(event.player, event.block)) {
            val block = event.block
            BlockListener.brokenBlocks[block.location] = BlockBean(block)
            block.type = Material.AIR
            if (ALLOW_DROPS) block.world.dropItem(block.location, ItemStack(Material.COBWEB, 1))
        }
    }

    private fun allowCobweb(player: Player, block: Block): Boolean {
        return isAllowed(player, IS_SERVER_WIDE, IS_IN_GAME_ONLY)
                && block.type == Material.COBWEB
                && player.inventory.itemInMainHand.type == Material.getMaterial(ALLOWED_TOOL.toString().uppercase())
    }
}