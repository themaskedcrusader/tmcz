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

package com.themaskedcrusader.tmcz.modules.game;

import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.plugin.java.JavaPlugin;

public class Game {
    public static final String SYSTEM          = "game-system";
    public static final String ENABLED         = ".enabled";
    public static final String RETURN_TO_SPAWN = SYSTEM + ".return-to-spawn";
    public static final String ONLY_ZOMBIES    = SYSTEM + ".only-zombies";
    public static final String PVP             = SYSTEM + ".pvp";

    public static final String SUGAR_SPEED = SYSTEM + ".sugar-speed";
    public static final String DURATION = ".duration";
    public static final String MULTIPLIER = ".multiplier";

    private static final String START_KIT  = ".start-kit";
    public static final String S_ENABLED   = SYSTEM + START_KIT + ENABLED;
    public static final String S_HELMET    = SYSTEM + START_KIT + ".helmet";
    public static final String S_ARMOR     = SYSTEM + START_KIT + ".armor";
    public static final String S_LEGGINGS  = SYSTEM + START_KIT + ".leggings";
    public static final String S_BOOTS     = SYSTEM + START_KIT + ".boots";
    public static final String S_ITEMS     = SYSTEM + START_KIT + ".items";

    private static final String SPAWN    = ".spawn";
    public static final String TELEPORT  = SYSTEM + SPAWN + ".teleport";
    public static final String RANDOM    = SYSTEM + SPAWN + ".random";
    public static final String ZONE1     = SYSTEM + SPAWN + ".zone1";
    public static final String ZONE2     = SYSTEM + SPAWN + ".zone2";
    public static final String STATIC    = SYSTEM + SPAWN + ".static-locations";

    // TAG LIMITS
    public static final String BANDIT_TAG   = SYSTEM + ".bandit-tag";
    public static final String BANDIT_KILLS = SYSTEM + ".bandit_kills";
    public static final String BANDIT_LOSE  = SYSTEM + ".bandit-lose-tag";
    public static final String BANDIT_HEALS = SYSTEM + ".bandit-healer";
    public static final String HEALER_TAG   = SYSTEM + ".healer-tag";
    public static final String HEALER_HEALS = SYSTEM + ".healer-heals";


    // MESSAGES
    public static final String MOTD         = SYSTEM + ".welcome-message";
    public static final String KILLS        = SYSTEM + ".kill-stats";
    public static final String ZOMBIE_KILLS = SYSTEM + ".zombie-kills";

    private Game() {}

    public static void initialize(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(SYSTEM + ENABLED)) {
            new WorldRules(plugin);
            new GameListener(plugin);
            if (Settings.getConfig().getBoolean(SUGAR_SPEED + ENABLED)) {
                new SugarSpeedListener(plugin);
            }
            plugin.getLogger().info("GAME SYSTEM ONLINE");
        }
    }
}
