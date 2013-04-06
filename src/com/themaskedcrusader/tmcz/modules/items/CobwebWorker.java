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

public class CobwebWorker extends Items implements Listener {

    // Configuration variables
    private static final String COBWEB   = ".cobweb";
    public static final String _ENABLED  = SYSTEM + COBWEB + ENABLED;
    public static final String _IN_GAME  = SYSTEM + COBWEB + IN_GAME;
    public static final String _SERVER   = SYSTEM + COBWEB + SERVER_WIDE;
    public static final String _TOOL     = SYSTEM + COBWEB + TOOL_ID;
    public static final String _DROP     = SYSTEM + COBWEB + ".drop";
    public static final String _RESPAWN  = SYSTEM + COBWEB + RESPAWN;
    public static final String _SECONDS  = SYSTEM + COBWEB + R_SECONDS;


    public CobwebWorker(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(_ENABLED)) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);

            if (Settings.getConfig().getBoolean(_RESPAWN)) {
                plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        CobwebWorker.cleanUp();
                    }
                }, 15L, Settings.getConfig().getLong(_SECONDS) * 20L);
            }
        }
    }

    @EventHandler
    public void breakCobweb(PlayerInteractEvent event) {
        if (allowCobweb(event.getPlayer(), event.getClickedBlock())) {
            Block block = event.getClickedBlock();
            BlockWorker.brokenBlocks.put(block.getLocation(), new BlockBean(block));
            block.setType(Material.AIR);
            if (Settings.getConfig().getBoolean(_DROP)) {
                block.getWorld().dropItem(
                        block.getLocation(), new ItemStack(Material.WEB, 1));
            }
        }
    }

    public boolean allowCobweb(Player player, Block clickedBlock) {
        return isAllowed(player, _SERVER, _IN_GAME) && clickedBlock != null &&
                clickedBlock.getType() == Material.WEB &&
                player.getItemInHand().getType() ==
                        Material.getMaterial(Settings.getConfig().getInt(_TOOL));
    }

    public static void cleanUp() {
        for (Map.Entry<Location, BlockBean> entry : BlockWorker.brokenBlocks.entrySet()) {
            BlockBean block = entry.getValue();
            if (block.getMaterial() == Material.WEB &&
                    new Date().getTime() - block.getDate().getTime() > Settings.getConfig().getLong(_SECONDS) * 1000) {
                entry.getKey().getBlock().setType(entry.getValue().getMaterial());
                BlockWorker.brokenBlocks.remove(entry.getKey());
            }
        }
    }
}
