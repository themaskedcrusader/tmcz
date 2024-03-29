/*
 * Copyright (c) 2013 - 2023 Christopher Schalk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.schalk.spigot.tmcz.modules.mob

import me.schalk.spigot.lib.config.getSettings
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

abstract class MobModule : Listener {
    companion object {
        lateinit var plugin: JavaPlugin

        const val MODULE  = "mob-controls"
        const val ENABLED = ".enabled"
        const val ZOMBIE = ".zombie"

        fun initialize(plugin: JavaPlugin) {
            MobModule.plugin = plugin
            if (!getSettings().getConfig().getBoolean(MODULE + ZOMBIE + ENABLED)) {
                ZombieListener(plugin)
            }
        }
    }
}