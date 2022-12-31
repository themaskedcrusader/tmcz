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

package me.schalk.spigot.tmcz.modules.bleed

import me.schalk.spigot.lib.config.getMessages
import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.lib.math.getChancePercentage
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class BleedListener(plugin: JavaPlugin) : BleedModule() {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun bleedPlayer(event: EntityDamageEvent) {
        if (isAllowed(event.entity)) {
            val player = event.entity as Player
            val chanceConfiguration: Int = getSettings().getConfig().getInt(MODULE + CHANCE)
            if (chanceConfiguration > getChancePercentage() && !GameData.getPlayer(event.entity as Player).bleeding) {
                if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    || event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                    || event.cause == EntityDamageEvent.DamageCause.FALL
                    || event.cause == EntityDamageEvent.DamageCause.FIRE_TICK
                    || event.cause == EntityDamageEvent.DamageCause.PROJECTILE)
                {
                    val seconds: Int = getSettings().getConfig().getInt(MODULE + SECONDS) * 20
                    val bleed = PotionEffect(PotionEffectType.BLINDNESS!!, seconds, 1)
                    player.sendMessage(ChatColor.RED.toString() + getMessages().getConfig().getString(MODULE + HIT_MSG))
                    player.addPotionEffect(bleed)
                    GameData.getPlayer(event.entity as Player).bleeding = true
                }
            }
        }
    }

    @EventHandler
    fun stopBleedWithBandage(event: PlayerInteractEvent) {
        if (isAllowed(event.player)) {
            val player = event.player
            if ((event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) &&
                player.inventory.itemInMainHand.type == Material.PAPER // TODO: update this to configured healer item
            ) {
                useBandage(player)
            }
        }
    }

    private fun useBandage(player: Player) {
        if (GameData.getPlayer(player).bleeding) {
            GameData.getPlayer(player).bleeding = false
            val regen = PotionEffect(PotionEffectType.REGENERATION, 4, 2)
            player.addPotionEffect(regen)
            if (player.inventory.itemInMainHand.amount > 1) {
                player.inventory.itemInMainHand.amount--
            } else {
                player.inventory.itemInMainHand.type = Material.AIR
            }
            player.sendMessage(ChatColor.GREEN.toString() + getMessages().getConfig().getString(MODULE + SELF_STOP))
        }
    }
}