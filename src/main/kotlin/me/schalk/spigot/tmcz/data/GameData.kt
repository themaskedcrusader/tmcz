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

package me.schalk.spigot.tmcz.data

import org.bukkit.entity.Player

sealed class GameData {
    data class PlayerBean(val playerId: String) {
        var antibiotic = false
        var bandaged = false
        var bleeding = false
        var infected = false
        var ointment = false
        var thirsty = false
        var drugged_doses = 0
        var drugged_cooldown = 0
        var tolerance = false
        var overdose = false
        var zombieKills = 0
            private set
        var playerKills = 0
            private set
        var playerHeals = 0
            private set
        var playing = false

        fun addZombieKill() {
            zombieKills++
        }

        fun addPlayerKills() {
            playerKills++
        }

        fun addPlayerHeals() {
            playerHeals++
        }
    }

    companion object {
        private val players = mutableMapOf<String, PlayerBean>()

        fun getPlayer(player: Player) : PlayerBean {
            var ret = players[player.displayName]
            if (ret == null) {
                ret = PlayerBean(player.displayName)
                players[ret.playerId] = ret
            }
            return ret
        }
    }
}