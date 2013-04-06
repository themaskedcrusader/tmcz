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
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Items {

    protected static final String SYSTEM      = "item-system";
    protected static final String ENABLED     = ".enabled";
    protected static final String IN_GAME     = ".only-in-game";
    protected static final String SERVER_WIDE = ".server-wide";
    protected static final String RESPAWN     = ".respawn";
    protected static final String R_SECONDS   = ".respawn-seconds";
    protected static final String TOOL_ID     = ".tool-id";

    // Suppress instantiability of Utility Class
    protected Items() {}

    public static void initialize(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(SYSTEM + ENABLED)) {
            new ItemsWorker(plugin);
            new GrenadeWorker(plugin);  // Confirmed
            new MelonWorker(plugin);
            new MushroomWorker(plugin);
            new CobwebWorker(plugin);
            new BlockWorker(plugin);
            plugin.getLogger().info("Registered custom item interactions!");
        }
    }

    protected static boolean isAllowed(Player player, String serverWide, String inGame) {
        boolean worldAllowed = WorldUtils.isAllowed(player.getWorld(), Settings.get());
        if (Settings.getConfig().getBoolean(serverWide)) return true;
        if (Settings.getConfig().getBoolean(inGame)) return PlayerUtil.isPlaying(player) && worldAllowed;
        return worldAllowed;
    }
}
