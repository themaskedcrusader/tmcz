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

package com.themaskedcrusader.tmcz.data;

import com.themaskedcrusader.bukkit.config.ConfigAccessor;
import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.StringUtils;
import com.themaskedcrusader.tmcz.modules.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PlayerUtil {
    private static Map<String, PlayerBean> players = new HashMap<String, PlayerBean>();

    // Suppress instantiability of Utility Class
    private PlayerUtil() {}

    public static Map<String, PlayerBean> getPlayers() {
        return players;
    }

    public static void putPlayer(PlayerBean player) {
        players.put(player.getPlayerId(), player);
    }

    public static PlayerBean getPlayer(String playerId) {
        return players.get(playerId);
    }

    public static PlayerBean getPlayer(Player player) {
        PlayerBean toReturn = players.get(player.getDisplayName());
        if (toReturn == null) {
            initPlayer(player);
        }
        return players.get(player.getDisplayName());
    }

    public static boolean isPlaying(Player player) {
        PlayerBean pb = getPlayer(player);
        if (pb != null) {
            return pb.isPlaying();
        } else {
            return false;
        }
    }

    public static void quitGame(Player player) {
        PlayerBean pb = getPlayer(player);
        pb.setPlaying(false);
    }

    public static void startGame(Player player) {
        PlayerBean pb = getPlayer(player);
        if (pb != null) {
            pb.setPlaying(true);
        } else {
            // TODO: this may cause an infinite loop... test thoroughly
            initPlayer(player);
            startGame(player);
        }
    }

    public static boolean isStatusSet(Player player, Status status) {
        PlayerBean bean = getPlayer(player);
        try {
            String readName = "is" + StringUtils.capitalize(status.toString());
            Method read = bean.getClass().getMethod(readName);
            return (Boolean) read.invoke(bean);
        } catch (Exception ignored) {
            System.err.println("Error getting status: -- " + ignored.getMessage());
            return false;
        }
    }

    public static void setStatus(Player player, Status status, boolean val) {
        PlayerBean bean = players.get(player.getDisplayName());
        try {
            String writeName = "set" + StringUtils.capitalize(status.toString());
            Method write = bean.getClass().getMethod(writeName, Boolean.class);
            write.invoke(bean, val);
        } catch (Exception ignored) {
            System.err.println("Error setting status: -- " + ignored.getMessage());
        }
    }

    public static void addZombieKill(Player player) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            bean.addZombieKill();
        }
    }

    public static int getZombieKills(Player player) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            return bean.getZombieKills();
        }
        return 0;
    }

    public static void addPlayerKill(Player player, ConfigAccessor settings) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            bean.addPlayerKill();
            //            tagKiller(player, settings);
        }
    }

    public static int getPlayerKills(Player player) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            return bean.getPlayerKills();
        }
        return 0;
    }

    public static void addPlayerHeal(Player player, ConfigAccessor settings) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            bean.addPlayerHeal();
            //            tagHealer(player, settings);
        }
    }

    public static int getPlayerHeals(Player player) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            return bean.getPlayerHeals();
        }
        return 0;
    }

    public static void echoKillStats(Player player, ConfigAccessor messages) {
        if (isPlaying(player)) {
            PlayerBean bean = players.get(player.getDisplayName());
            String message = ChatColor.YELLOW + messages.getConfig().getString("player.kill-stats");
            message = message.replace("@zombie", "" + bean.getZombieKills());
            message = message.replace("@player", "" + bean.getPlayerKills());
            player.sendMessage(message);
        } else {
            player.sendMessage(ChatColor.YELLOW + "You are not currently playing the game. Type /spawn to join");
        }
    }

    public static boolean isBandit(Player player, ConfigAccessor settings) {
        return isPlaying(player) && getPlayerKills(player) > settings.getConfig().getInt(Game.BANDIT_KILLS);
    }

    private static boolean isHealer(Player player) {
        return (isBandit(player, Settings.get()))
                ? Settings.getConfig().getBoolean(Game.BANDIT_LOSE) && getPlayerHeals(player) > Settings.getConfig().getInt(Game.BANDIT_HEALS)
                : isPlaying(player) && getPlayerHeals(player) > Settings.getConfig().getInt(Game.HEALER_HEALS);
    }

    public static void initPlayer(Player player) {
        if (players.get(player.getDisplayName()) == null) {
            PlayerBean pb = new PlayerBean(player.getDisplayName());
            players.put(player.getDisplayName(), pb);
        }
    }

    public static void removePlayer(Player player) {
        if (isPlaying(player)) {
            players.remove(player.getDisplayName());
        }
    }

    public static void sendKillMessage(Player player) {
        String killMessage = Messages.getConfig().getString(Game.KILLS);
        killMessage = killMessage.replace("@zombie", "" + getZombieKills(player));
        killMessage = killMessage.replace("@player", "" + getPlayerKills(player));
        player.sendMessage(ChatColor.YELLOW + killMessage);
    }
}