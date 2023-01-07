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
import me.schalk.spigot.tmcz.data.GameData
import me.schalk.spigot.tmcz.settings.Permissions
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class BlockListener(plugin: JavaPlugin) : ItemModule() {

    companion object {
        private const val BLOCK_PROTECTION      = MODULE +".block-protection"
        private const val _CAN_PLACE_BLOCKS     = BLOCK_PROTECTION + ".place"
        private const val _CAN_BREAK_BLOCKS     = BLOCK_PROTECTION + ".break"
        private const val _IN_GAME_ONLY         = BLOCK_PROTECTION + IN_GAME
        private const val _SERVER_WIDE          = BLOCK_PROTECTION + SERVER_WIDE
        private const val _CAN_OP_BUILD         = BLOCK_PROTECTION + ".op-can-build"
        private const val _ALLOWED_ITEMS_CONFIG = BLOCK_PROTECTION + ".allowed-items"

        private val allowedItems = mutableMapOf<Material, BlockBean>()
        private val placedBlocks = mutableMapOf<Location, BlockBean>()
        val brokenBlocks = mutableMapOf<Location, BlockBean>()

        fun cleanUpBlocks(force: Boolean) {
            cleanUpPlacedBlocks(force)
            cleanUpBrokenBlocks(force)
        }

        private fun cleanUpPlacedBlocks(force: Boolean) {
            val now = Date().time
            placedBlocks.forEach { placedBlock ->
                try {
                    val location = placedBlock.key
                    val block = placedBlock.value
                    val data = allowedItems[block.material]
                    val place: Long = block.date.time
                    if (force || now - place > (data?.despawnSeconds?.times(1000)!!)) {
                        location.world!!.getBlockAt(location).type = Material.AIR
                        placedBlocks.remove(location)
                    }
                } catch (ignored: ConcurrentModificationException) {
                    // prevent CME from the log - try again next loop
                }
            }
        }

        private fun cleanUpBrokenBlocks(force: Boolean) {
            val now = Date().time
            try {
                brokenBlocks.forEach { brokenBlock ->
                    val location = brokenBlock.key
                    val block = brokenBlock.value
                    val data = allowedItems[block.material]
                    val broken: Long = block.date.time
                    if (force || now - broken > (data?.respawnSeconds?.times(1000)!!)) {
                        val toRepair = location.world!!.getBlockAt(location)
                        toRepair.type = block.material
                        toRepair.blockData = block.data
                        placedBlocks.remove(location)
                    }
                }
            } catch (ignored: ConcurrentModificationException) {
                // prevent CME from the log - try again next loop
            }
        }
    }

    init {
        registerAllowedItems(plugin)
        plugin.server.pluginManager.registerEvents(this, plugin)
        if (getSettings().getConfig().getBoolean(_CAN_PLACE_BLOCKS)) {
            plugin.server.scheduler.scheduleSyncRepeatingTask(plugin,
                { cleanUpPlacedBlocks(false) }, 15L, 600L
            )
        }
        if (getSettings().getConfig().getBoolean(_CAN_BREAK_BLOCKS)) {
            plugin.server.pluginManager.registerEvents(this, plugin)
            plugin.server.scheduler.scheduleSyncRepeatingTask(plugin,
                { cleanUpBrokenBlocks(false) }, 15L, 600L
            )
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.isCancelled = trackBlockInteraction(event.player, event.blockPlaced, placedBlocks, true)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (placedBlocks.containsKey(event.block.location)) {
            breakPlacedBlock(event) // Break
        } else {
            val blocked = trackBlockInteraction(event.player, event.block, brokenBlocks, false)
            if (blocked) {
                event.isCancelled = true
            } else {
                if (event.block.type == Material.TORCH) {
                    event.isCancelled = true
                    event.block.type = Material.AIR
                }
            }
        }
    }

    // Return value is whether the action is blocked (false = not blocked, true = is blocked)
    private fun trackBlockInteraction(player: Player, block: Block, tracker: MutableMap<Location, BlockBean>, place: Boolean): Boolean {
        if (isBlockProtectionEnabled(player)) {
            if (player.isPermissionSet(Permissions.BUILDER) || (getSettings().getConfig().getBoolean(_CAN_OP_BUILD) && player.isOp)) {
                // Player has tmcz.builder permission or is OP and OP can build
                return false

            } else if (getSettings().getConfig().getBoolean(_IN_GAME_ONLY) && !GameData.getPlayer(player).playing) {
                // player is not playing, block the event
                return true

            } else {
                val allowed: BlockBean? = allowedItems[block.type]

                if (allowed == null) {
                    // Item is not in allowed list, block the event
                    return true
                } else {
                    if (place && allowed.despawnSeconds == 0) {
                        // Placing an Allowed block that will despawn immediately, cancel the event
                        return true

                    } else if (place && allowed.despawnSeconds == -1) {
                        // Placing an Allowed block will never despawn, allow event without scheduling despawn
                        return false

                    } else if (!place && allowed.respawnSeconds == 0) {
                        // Breaking an Allowed block that will respawn immediately, cancel the event
                        return true

                    } else if (!place && allowed.respawnSeconds == -1) {
                        // Breaking an Allowed block that will never respawn, allow event without scheduling respawn
                        return false

                    } else {
                        // Allowed block, schedule cleanup
                        tracker[block.location] = BlockBean(block)
                    }
                }
            }
        }
        return false    // default - do not cancel event
    }

    private fun breakPlacedBlock(event: BlockBreakEvent) {
        if (!event.player.hasPermission(Permissions.BUILDER)
            && (!getSettings().getConfig().getBoolean(_CAN_OP_BUILD) || !event.player.isOp)
            && isBlockProtectionEnabled(event.player)
        ) {
            if (placedBlocks.containsKey(event.block.location)) {
                if (getSettings().getConfig().getBoolean(_IN_GAME_ONLY) && !GameData.getPlayer(event.player).playing) {
                    event.isCancelled = true
                } else {
                    placedBlocks.remove(event.block.location)
                }
            } else {
                event.isCancelled = true
            }
        }
    }

    private fun isBlockProtectionEnabled(player: Player): Boolean {
        return isAllowed(player, _SERVER_WIDE, _IN_GAME_ONLY)
    }

    private fun registerAllowedItems(plugin: JavaPlugin) {
        val config: List<String> = getSettings().getConfig().getStringList(_ALLOWED_ITEMS_CONFIG)
        for (itemString in config) {
            try {
                val item = itemString.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val blockName = Material.getMaterial(item[0].uppercase())
                val despawnSeconds = item[1].toInt()
                val respawnSeconds = item[2].toInt()
                allowedItems[blockName!!] = BlockBean(blockName, despawnSeconds, respawnSeconds)
            } catch (e: Exception) {
                plugin.logger.info("Error parsing allowed items: incorrect format - [$itemString]")
            }
        }
    }
}