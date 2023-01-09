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

package me.schalk.spigot.tmcz.modules.items

import me.schalk.spigot.lib.config.getSettings
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.plugin.java.JavaPlugin

class GrenadeListener(plugin: JavaPlugin) : ItemModule() {

    companion object {
        private const val GRENADE       = MODULE +".end-grenade"
        private val IS_ENABLED          = getSettings().getConfig().getBoolean(GRENADE + ENABLED)
        private val IS_IN_GAME_ONLY     = getSettings().getConfig().getBoolean(GRENADE + IN_GAME_ONLY)
        private val IS_SERVER_WIDE      = getSettings().getConfig().getBoolean(GRENADE + SERVER_WIDE)
        private val IS_PROTECT_WORLD    = getSettings().getConfig().getBoolean(GRENADE + ".protect-world")
        private val BLAST_RADIUS        = getSettings().getConfig().getInt(GRENADE + ".radius")
    }

    init {
        if (IS_ENABLED) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }
    }

    @EventHandler
    fun enderPearlGrenade(event: PlayerTeleportEvent) {
        if (allowGrenade(event.player, event.cause)) {
            event.isCancelled = true
            val toExplode = event.to
            if (toExplode != null) {
                toExplode.world?.createExplosion(toExplode, BLAST_RADIUS.toFloat(), !IS_PROTECT_WORLD, !IS_PROTECT_WORLD)
            }
        }
    }

    private fun allowGrenade(player: Player, cause: TeleportCause): Boolean {
        return isAllowed(player, IS_SERVER_WIDE, IS_IN_GAME_ONLY) && cause == TeleportCause.ENDER_PEARL
    }
}