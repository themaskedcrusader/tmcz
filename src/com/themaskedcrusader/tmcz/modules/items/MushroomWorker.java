/*
 * Copyright 2013 Topher Donovan (themaskedcrusader.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.themaskedcrusader.tmcz.modules.items;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.tmcz.data.BlockBean;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.Map;

public class MushroomWorker extends Items implements Listener {

    private static final String MUSHROOM = ".mushroom";
    public static final String _ENABLED = SYSTEM + MUSHROOM + ENABLED;
    public static final String _IN_GAME = SYSTEM + MUSHROOM + IN_GAME;
    public static final String _SERVER  = SYSTEM + MUSHROOM + SERVER_WIDE;
    public static final String _TOOL    = SYSTEM + MUSHROOM + TOOL_ID;
    public static final String _RESPAWN = SYSTEM + MUSHROOM + RESPAWN;
    public static final String _SECONDS = SYSTEM + MUSHROOM + R_SECONDS;

    public MushroomWorker(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(_ENABLED)) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);

            if (Settings.getConfig().getBoolean(_RESPAWN)) {
                plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        MushroomWorker.cleanUp();
                    }
                }, 15L, Settings.getConfig().getLong(_SECONDS) * 20L);
            }
        }
    }

   @EventHandler
    public void harvestMushroom(PlayerInteractEvent event) {
        if (allowMushroom(event.getPlayer(), event.getClickedBlock())) {
            Block block = event.getClickedBlock();
            Material type = block.getType();
            block.setType(Material.AIR);
            block.getWorld().dropItem(block.getLocation(), new ItemStack(type, 1));
            BlockWorker.brokenBlocks.put(block.getLocation(), new BlockBean(block));
        }
    }

    public static boolean allowMushroom(Player player, Block clickedBlock) {
        return isAllowed(player, _SERVER, _IN_GAME) && clickedBlock != null &&
                (clickedBlock.getType() == Material.RED_MUSHROOM ||
                        clickedBlock.getType() == Material.BROWN_MUSHROOM) &&
                player.getItemInHand().getType() ==
                        Material.getMaterial(Settings.getConfig().getInt(_TOOL));
    }

    public static void cleanUp() {
        for (Map.Entry<Location, BlockBean> entry : BlockWorker.brokenBlocks.entrySet()) {
            BlockBean block = entry.getValue();
            if ((block.getMaterial() == Material.BROWN_MUSHROOM || block.getMaterial() == Material.RED_MUSHROOM) &&
                    new Date().getTime() - block.getDate().getTime() > Settings.getConfig().getLong(_SECONDS) * 1000) {
                entry.getKey().getBlock().setType(block.getMaterial());
                BlockWorker.brokenBlocks.remove(entry.getKey());
            }
        }
    }
}