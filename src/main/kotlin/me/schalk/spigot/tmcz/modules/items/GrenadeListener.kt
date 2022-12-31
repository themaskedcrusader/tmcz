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
        private const val GRENADE         = ".end-grenade"
        private const val _ENABLED        = MODULE + GRENADE + ENABLED
        private const val _IN_GAME_ONLY   = MODULE + GRENADE + IN_GAME
        private const val _SERVER_WIDE    = MODULE + GRENADE + SERVER_WIDE
        private const val _PROTECT_WORLD  = MODULE + GRENADE + ".protect-world"
        private const val _BLAST_RADIUS   = MODULE + GRENADE + ".radius"
    }

    init {
        if (getSettings().getConfig().getBoolean(_ENABLED)) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }
    }

    @EventHandler
    fun enderPearlGrenade(event: PlayerTeleportEvent) {
        if (allowGrenade(event.player, event.cause)) {
            event.isCancelled = true
            val toExplode = event.to
            val isWorldProtected = getSettings().getConfig().getBoolean(_PROTECT_WORLD)
            toExplode!!.world!!.createExplosion(toExplode, getSettings().getConfig().getDouble(_BLAST_RADIUS).toFloat(),
                !isWorldProtected, !isWorldProtected)
        }
    }

    private fun allowGrenade(player: Player, cause: TeleportCause): Boolean {
        return isAllowed(player, _SERVER_WIDE, _IN_GAME_ONLY) && cause == TeleportCause.ENDER_PEARL
    }
}