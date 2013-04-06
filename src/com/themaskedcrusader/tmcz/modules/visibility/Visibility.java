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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Visibility {
    // Constants for the visibility system
    private static final String SYSTEM      = "visibility-system";
    public static final String ENABLED      = SYSTEM + ".enabled";
    public static final String IN_GAME      = SYSTEM + ".only-in-game";
    public static final String SERVER_WIDE  = SYSTEM + ".server-wide";
    public static final String TICKS        = SYSTEM + ".ticks";

    // Suppress instantiability of Utility Class
    private Visibility() {}

    public static void initialize(JavaPlugin plugin) {
        if (WorldUtils.isSingleWorld(Settings.get())) {
            if (Settings.getConfig().getBoolean(ENABLED)) {
                new VisibilitySchedule(plugin);
    //            new VisibilityListener(plugin);
//                plugin.getLogger().info("Visibility System Online!");
            }
        } else {
            plugin.getLogger().info("Multi-world enabled, Cannot load Visibility System");
        }
    }

    public static boolean isAllowed(Entity entity) {
        boolean worldAllowed = WorldUtils.isAllowed(entity.getWorld(), Settings.get());
        if (entity.getType() != EntityType.PLAYER) return false;
        if (Settings.getConfig().getBoolean(SERVER_WIDE)) return true;
        if (Settings.getConfig().getBoolean(IN_GAME)) return PlayerUtil.isPlaying((Player) entity) && worldAllowed;
        return worldAllowed;
    }

}
