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

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MelonWorker extends Items implements Listener {

    private static final String MELON    = ".melon";
    public static final String _ENABLED  = SYSTEM + MELON + ENABLED;
    public static final String _IN_GAME  = SYSTEM + MELON + IN_GAME;
    public static final String _SERVER   = SYSTEM + MELON + SERVER_WIDE;
    public static final String _TOOL     = SYSTEM + MELON + TOOL_ID;


    public MelonWorker(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(_ENABLED)) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void harvestMelon(PlayerInteractEvent event) {
        if (allowMelon(event.getPlayer(), event.getClickedBlock())) {
            Block block = event.getClickedBlock();
            block.setType(Material.AIR);
            block.getWorld().dropItem(
                    block.getLocation(), new ItemStack(Material.MELON, new Random().nextInt(3) + 1));
        }
    }

    public static boolean allowMelon(Player player, Block clickedBlock) {
        return isAllowed(player, _SERVER, _IN_GAME) && clickedBlock != null &&
                clickedBlock.getType() == Material.MELON_BLOCK &&
                player.getItemInHand().getType() ==
                        Material.getMaterial(Settings.getConfig().getInt(_TOOL)) ;
    }
}
