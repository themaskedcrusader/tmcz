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
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.settings.Permissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemsWorker implements Listener {

    public ItemsWorker(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void preventAllBlockBreaking(BlockBreakEvent event) {
        if (WorldUtils.isAllowed(event.getPlayer().getWorld(), Settings.get()) &&
                (!event.getPlayer().hasPermission(Permissions.BUILDER) && !event.getPlayer().isOp())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void preventAllBlockPlacing(BlockPlaceEvent event) {
        if (WorldUtils.isAllowed(event.getPlayer().getWorld(), Settings.get()) &&
                (!event.getPlayer().hasPermission(Permissions.BUILDER) && !event.getPlayer().isOp())) {
            event.setCancelled(true);
        }
    }
}
