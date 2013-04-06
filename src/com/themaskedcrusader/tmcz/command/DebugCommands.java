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

package com.themaskedcrusader.tmcz.command;

import com.themaskedcrusader.bukkit.chest.MaskedItem;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.data.Status;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DebugCommands extends CommandProcessor {

    public DebugCommands(JavaPlugin plugin) {
       super(plugin);
    }

    public static void giveItem(Player player, String[] args) {
        try {
            String item = args[1];
            ItemStack toGive = new MaskedItem(item).unmask();
            player.setItemInHand(toGive);
        } catch (Exception ignored) {}
    }

    public static void potionEffect(CommandSender sender, String[] args) {
        try {
            int id = Integer.parseInt(args[1]);
            int amp = 1;
            int duration = 300;
            String playerName = null;
            try {
                amp = Integer.parseInt(args[2]);
                duration = Integer.parseInt(args[3]);
                playerName = args[3];
            } catch (Exception ignored) {}

            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getById(id), duration, amp);
            Player player;
            if (playerName != null) {
                player = plugin.getServer().getPlayer(playerName);
            } else if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                throw new Exception("No Valid Player");
            }

            player.addPotionEffect(potionEffect);

        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Command Format: /tmcz potion <id> [<level> <duration> <player name>]");
        }
    }

    public static void infectPlayer(CommandSender sender, String[] args) {
        if (args != null) {
            Player toInfect = plugin.getServer().getPlayer(args[1]);
            if (toInfect != null) {
                infectPlayer(toInfect);
            } else {
                sender.sendMessage(ChatColor.RED + "[DEBUG] Can't find player with name" + args[0]);
            }
        } else if (sender instanceof Player) {
            infectPlayer((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Command Format: /tmcz infect <player_name>");
        }
    }

    private static void infectPlayer(Player player) {
        if (PlayerUtil.isPlaying(player)) {
            PlayerUtil.setStatus(player, Status.INFECTED, true);
        } else {
            player.sendMessage("[DEBUG] Can't infect player, not in game");
        }
    }

    public static void setFoodLevel(CommandSender sender, String[] args) {
        int argLevel = 1;
        Integer level = -1;
        String playerName= null;

        // get Player
        try {
            playerName = args[argLevel];
            argLevel++;
        } catch (Exception ignored) {}

        try {
            level = Integer.parseInt(args[argLevel]);
        } catch (Exception ignored) {
            sender.sendMessage(ChatColor.RED + "[DEBUG] Command format: /tmcz food <playerName> <level>");
            return;
        }

        Player toUpdate = null;
        if (playerName != null) {
            toUpdate = plugin.getServer().getPlayer(playerName);
        } else if (sender instanceof Player) {
            toUpdate = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Please use this command as a logged in player");
        }

        toUpdate.setFoodLevel(level);

    }
}
