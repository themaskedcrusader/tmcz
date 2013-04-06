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

package com.themaskedcrusader.tmcz.modules.visibility;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class VisibilitySchedule {
    private static JavaPlugin plugin;
    static Map<String, Location> playerMovement = new HashMap<String, Location>();

    public VisibilitySchedule(JavaPlugin plugin) {
        VisibilitySchedule.plugin = plugin;

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                VisibilitySchedule.calculateVisibility();
            }
        }, 20L, Settings.getConfig().getLong(Visibility.TICKS));
    }

    public static void calculateVisibility() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            if (Visibility.isAllowed(player)) {
                float visibility = calculateVisibility(player);
                player.setExp(visibility / 7);
                playerMovement.put(player.getDisplayName(), player.getLocation());
            }
        }
    }

    private static float calculateVisibility(Player player) {
        float visibility = 2;
        Location loc = playerMovement.get(player.getDisplayName());
        if (((!Settings.getConfig().getBoolean(Visibility.IN_GAME)) || PlayerUtil.isPlaying(player)) && loc != null) {
            if (WorldUtils.isPlayerMoving(player, loc))  { visibility += 1;     }
            if (WorldUtils.isPlayerFalling(player, loc)) { visibility += 1;     }
            if (player.isSneaking())                     { visibility -= 1;     }
            if (player.isSprinting())                    { visibility += 2;     }
            if (player.getWorld().isThundering())        { visibility -= 0.5F;  }
            return visibility;
        }
        return 0f;
    }
}
