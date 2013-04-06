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

package com.themaskedcrusader.tmcz.modules.healer;

import org.bukkit.plugin.java.JavaPlugin;

public class Healer {

    public static final String ANTIBIOTIC = "infection-system.healer-item";
    public static final String OINTMENT   = "bleed-system.healer-item";
    public static final String HEALED     = "(a)healed";
    public static final String HEALER     = "(a)healer";

    public static final String HEALER_START      = "heal.start.healer";
    public static final String HEALED_START      = "heal.start.healed";
    public static final String HEALER_OINTMENT   = "heal.ointment.healer";
    public static final String HEALED_OINTMENT   = "heal.ointment.healed";
    public static final String HEALER_ANTIBIOTIC = "heal.antibiotic.healer";
    public static final String HEALED_ANTIBIOTIC = "heal.antibiotic.healed";
    public static final String HEALER_FINISH     = "heal.finish.healer";
    public static final String HEALED_FINISH     = "heal.finish.healed";
    public static final String HEALED_OOT        = "heal.out-of-time";
    public static final String HEALER_FAIL_1     = "heal.fail-1";
    public static final String HEALER_FAIL_2     = "heal.fail-2";

    // Suppress instantiability of Utility Class
    private Healer() {}

    public static void initialize(JavaPlugin plugin) {
        new HealerListener(plugin);
    }

// Order:
//   - Bandage = Regen 4 seconds
//   - Ointment = Stops Bleeding, Regen + 1 second
//   - Antibiotic = cures infection, regen + 1 second
//   - Tweezers = changes status and starts regen.

}
