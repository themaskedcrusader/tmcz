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

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Stack {

    private static final String SYSTEM      = "limit-stacks";
    public static final String LIMITED      = SYSTEM + ".enabled";
    public static final String IN_GAME      = SYSTEM + ".only-in-game";
    public static final String SERVER_WIDE  = SYSTEM + ".server-wide";

    private Stack() {}

    public static void initialize(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(LIMITED)) {
            new StackLimiter(plugin);
//            new StackSchedule(plugin);
        }
    }

    public static boolean isLimited(Player player) {
        boolean worldAllowed = WorldUtils.isAllowed(player.getWorld(),  Settings.get());
        if (Settings.getConfig().getBoolean(SERVER_WIDE)) return true;
        if (Settings.getConfig().getBoolean(IN_GAME)) return PlayerUtil.isPlaying(player) && worldAllowed;
        return worldAllowed;
    }
}
