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

package com.themaskedcrusader.tmcz.modules.mobs;

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.plugin.java.JavaPlugin;

public class Mobs {
    protected static JavaPlugin plugin;
    protected static Random random = new Random();

    protected static final String SYSTEM  = "mob-controls";

    protected static final String ZOMBIES = ".zombies";

    protected Mobs() {}

    public static void initialize(JavaPlugin plugin) {
        Mobs.plugin = plugin;

        if (!Settings.getConfig().getBoolean(SYSTEM + ZOMBIES + ".can-burn")) {
            new ZombieListener(plugin);
        }


    }
}
