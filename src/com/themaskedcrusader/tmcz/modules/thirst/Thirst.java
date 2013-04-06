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

package com.themaskedcrusader.tmcz.modules.thirst;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Thirst {

    // Settings for Thirst System
    private static final String SYSTEM       = "thirst-system";
    public static final String ENABLED       = SYSTEM + ".enabled";
    public static final String IN_GAME       = SYSTEM + ".only-in-game";
    public static final String SERVER_WIDE   = SYSTEM + ".server-wide";
    public static final String TICKS         = SYSTEM + ".thirst-ticks";
    public static final String DAMAGE_TICKS  = SYSTEM + ".damage-ticks";
    public static final String DAMAGE_HIT    = SYSTEM + ".damage-hit";
    public static final String FULL          = SYSTEM + ".full";
    public static final String REFILL        = SYSTEM + ".refill";
    public static final String START         = SYSTEM + ".start";
    public static final String PARCH_1       = SYSTEM + ".parch-1";
    public static final String PARCH_2       = SYSTEM + ".parch-2";
    public static final String PARCH_3       = SYSTEM + ".parch-3";
    public static final String PARCH_4       = SYSTEM + ".parch-4";
    public static final String DEATH_LVL     = SYSTEM + ".death-lvl";
    public static final String CHECK_TICKS   = SYSTEM + ".check-ticks";

    // Messages for Thirst System
    public static final String PARCH_1_MSG = SYSTEM + ".parch-1";
    public static final String PARCH_2_MSG = SYSTEM + ".parch-2";
    public static final String PARCH_3_MSG = SYSTEM + ".parch-3";
    public static final String PARCH_4_MSG = SYSTEM + ".parch-4";
    public static final String DEATH_MSG   = SYSTEM + ".death";
    public static final String REFILL_MSG  = SYSTEM + ".refill";

    // Suppress instantiability of Utility Class
    private Thirst() {}

    public static void initialize(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(ENABLED)) {
            new ThirstListener(plugin);
            new ThirstSchedule(plugin);
            plugin.getLogger().info("Thirst System Online!");
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
