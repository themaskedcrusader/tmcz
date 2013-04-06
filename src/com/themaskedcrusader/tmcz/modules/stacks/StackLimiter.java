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

package com.themaskedcrusader.tmcz.modules.stacks;

import com.themaskedcrusader.bukkit.config.ConfigAccessor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class StackLimiter implements Listener {
    HashMap<Integer, Integer> stackLimits;

    public StackLimiter(JavaPlugin plugin) {
        registerStackLimits(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // TODO: #######################
    // TODO: ####   LISTENERS   ####
    // TODO: #######################

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
//        event.
//        event.getItem().getItemStack().setAmount(1);
//        if (Stack.isLimited(player)) {
//            ItemStack item = event.getItem().getItemStack();
//            int limit = getLimit(item);
//            if (limit != -1) {
//                ItemStack leftOvers = addToInventory(player, item, limit);
//                event.getItem().setItemStack(leftOvers);
//                event.setCancelled(true);
//
//            }
//        }
    }

    @EventHandler
    public void onInventoryRearrange(InventoryClickEvent event) {
        Player player = event.getWhoClicked().getType() == EntityType.PLAYER ? (Player) event.getWhoClicked() : null;
        if (player != null && event.getInventory().getType() == InventoryType.PLAYER && Stack.isLimited(player)) {
            int clickedLimit = getLimit(event.getCurrentItem());
            int cursorLimit = getLimit(event.getCursor());

            // Rearrange with shift click
            if (event.isShiftClick() && event.getCursor() != null && cursorLimit != -1) {
                addToInventory(player, event.getCursor(), cursorLimit);
                event.setCancelled(true);
            }

            // Rearrange with left click
            if (event.isLeftClick() && event.getCurrentItem() != null && clickedLimit != -1) {
                addToStack(event.getCurrentItem(),  event.getCursor(), getLimit(event.getCurrentItem()));
                event.setCancelled(true);
            }
        }
    }


    // TODO: #######################
    // TODO: ####   UTILITIES   ####
    // TODO: #######################

    private void registerStackLimits(JavaPlugin plugin) {
        stackLimits = new HashMap<Integer, Integer>();
        ConfigAccessor stacks = new ConfigAccessor(plugin, "stack-sizes.yml");
        List<String> configuredLimits = stacks.getConfig().getStringList("stack-limits");
        for (String s : configuredLimits) {
            try {
                String[] lim = s.split("\\|");
                int id = Integer.parseInt(lim[0]);
                int limit = Integer.parseInt(lim[1]);
                stackLimits.put(id, limit);
            } catch (Exception e) {
                plugin.getLogger().info("[E] stack limit: incorrect format - [" + s + "]");
            }
        }
    }

    protected int getLimit(ItemStack item) {
        if (item == null) return -1;
        int itemId = item.getTypeId();
        return stackLimits.containsKey(itemId) ? stackLimits.get(itemId) : -1 ;
    }

    protected ItemStack addToInventory(Player player, ItemStack item, Integer limit) {
        for(ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.getAmount() < limit && item.getAmount() > 0) {
                addToStack(item, invItem, limit);
            }
        }

        ItemStack[] invItems = player.getInventory().getContents();
        for (int i = 0 ; i < invItems.length ; i++) {
            if (invItems[i] == null && item.getAmount() > 0) {
                invItems[i] = createLimitedStack(item, limit);
            }
        }
        player.getInventory().setContents(invItems);
        return item;
    }

    protected void addToStack(ItemStack item, ItemStack toAddTo, int limit) {
        if (item != null && toAddTo != null && item.getType() == toAddTo.getType()) {
            int invAmount = toAddTo.getAmount();
            toAddTo.setAmount(Math.min(invAmount + item.getAmount(), limit));
            item.setAmount(toAddTo.getAmount() - invAmount);
        }
    }

    protected ItemStack createLimitedStack(ItemStack item, int limit) {
        ItemStack toReturn = item.clone();
        toReturn.setAmount(Math.min(item.getAmount(),  limit));
        item.setAmount(item.getAmount() - toReturn.getAmount());
        return toReturn;
    }
}