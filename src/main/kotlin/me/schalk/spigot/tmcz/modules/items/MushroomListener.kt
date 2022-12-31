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
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.ConcurrentModificationException

class MushroomListener(plugin: JavaPlugin) : ItemModule() {

    companion object {
        private const val MUSHROOM          = ".mushroom"
        private const val _ENABLED          = MODULE + MUSHROOM + ENABLED
        private const val _IN_GAME_ONLY     = MODULE + MUSHROOM + IN_GAME
        private const val _SERVER_WIDE      = MODULE + MUSHROOM + SERVER_WIDE
        private const val _ALLOWED_TOOL     = MODULE + MUSHROOM + TOOL_ID
        private const val _CAN_RESPAWN      = MODULE + MUSHROOM + RESPAWN
        private const val _RESPAWN_SECONDS  = MODULE + MUSHROOM + R_SECONDS

        fun cleanUp() {
            BlockListener.brokenBlocks.forEach{ brokenBlock ->
                try {
                    val location = brokenBlock.key
                    val block = brokenBlock.value
                    if ((block.material == Material.BROWN_MUSHROOM || block.material == Material.RED_MUSHROOM)
                        && Date().time.minus(block.date.time) > getSettings().getConfig().getLong(_RESPAWN_SECONDS).times(1000)
                    ) {
                        location.world!!.getBlockAt(location).type = block.material
                        BlockListener.brokenBlocks.remove(location)
                    }
                } catch (ignored: ConcurrentModificationException) {
                    // prevent CMD from server log - try again next loop
                }
            }
        }
    }

    init {
        if (getSettings().getConfig().getBoolean(_ENABLED)) {
            plugin.server.pluginManager.registerEvents(this, plugin)
            if (getSettings().getConfig().getBoolean(_CAN_RESPAWN)) {
                plugin.server.scheduler.scheduleSyncRepeatingTask(plugin,
                    { cleanUp() }, 15L, getSettings().getConfig().getLong(_RESPAWN_SECONDS) * 20L
                )
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun harvestMushroom(event: PlayerInteractEvent) {
        if (allowMushroom(event.player, event.clickedBlock)) {
            val block = event.clickedBlock
            val type = block!!.type
            block.type = Material.AIR
            block.world.dropItem(block.location, ItemStack(type, 1))
            BlockListener.brokenBlocks[block.location] = BlockBean(block)
        }
    }

    private fun allowMushroom(player: Player, clickedBlock: Block?): Boolean {
        return isAllowed(player, _SERVER_WIDE, _IN_GAME_ONLY) && clickedBlock != null &&
                (clickedBlock.type == Material.RED_MUSHROOM || clickedBlock.type == Material.BROWN_MUSHROOM) &&
                player.inventory.itemInMainHand.type == getSettings().getConfig().getString(
            _ALLOWED_TOOL)?.let { Material.getMaterial(it.uppercase()) }
    }

    // TODO: Allow bone meal on Mushrooms, but instead of growing into big, multiply into many smaller ones nearby

}