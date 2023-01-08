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

import me.schalk.spigot.lib.math.getChancePercentage
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class BleedListener(val plugin: JavaPlugin) : BleedModule() {

    private val bandageRegen = PotionEffect(PotionEffectType.REGENERATION, 80, 2, false, false, false)
    private val healerReward = PotionEffect(PotionEffectType.ABSORPTION, 400, 0, false, false, false)

    private val bleedingCauses = listOf(
        EntityDamageEvent.DamageCause.ENTITY_ATTACK,
        EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
        EntityDamageEvent.DamageCause.FALL,
        EntityDamageEvent.DamageCause.FIRE_TICK,
        EntityDamageEvent.DamageCause.PROJECTILE,
        EntityDamageEvent.DamageCause.CONTACT,
    )

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun bleedPlayer(event: EntityDamageEvent) {
        if (isAllowed(event.entity)) {
            val player = event.entity as Player
            if (!GameData.getPlayer(event.entity as Player).bleeding && getChancePercentage() >= CHANCE_TO_BLEED) {
                if (event.cause in bleedingCauses) {
                    player.sendMessage(ChatColor.RED.toString() + HIT_MSG)
                    player.spawnParticle(Particle.DAMAGE_INDICATOR, player.location, PARTICLES, 0.2, 0.2, 0.2, 0.01)
                    GameData.getPlayer(event.entity as Player).bleeding = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun stopBleedingUponDeath(event: PlayerDeathEvent) {
        if (isAllowed(event.entity)) {
            if (GameData.getPlayer(event.entity).bleeding && event.entity.lastDamageCause?.cause == EntityDamageEvent.DamageCause.CUSTOM)
                event.deathMessage = DEATH_MESSAGE?.replace("__player", event.entity.displayName)
            GameData.getPlayer(event.entity).bleeding = false
        }
    }

    @EventHandler
    fun selfStopBleedingWithBandage(event: PlayerInteractEvent) {
        if (isAllowed(event.player)) {
            val player = event.player
            if ((event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) &&
                player.inventory.itemInMainHand.type == BANDAGE
            ) {
                useBandage(player)
            }
        }
    }

    @EventHandler
    fun healerStopsBleedingWithBandage(event: PlayerInteractEntityEvent) {
        if (isAllowed(event.player)) {
            val healer = event.player
            val healerName = healer.name
            if (event.rightClicked.type == EntityType.PLAYER && healer.inventory.itemInMainHand.type == BANDAGE) {
                val healedName = event.rightClicked.name
                val healed = Bukkit.getPlayerExact(healedName)
                if (healed != null) {
                    if (GameData.getPlayer(healed).bleeding) {
                        GameData.getPlayer(healed).bleeding = false
                        healed.sendMessage(ChatColor.GREEN.toString() + HEALED_MESSAGE?.replace("__healer", healerName))
                        healed.addPotionEffect(bandageRegen)

                        healer.inventory.itemInMainHand.amount--
                        healer.sendMessage(ChatColor.GREEN.toString() + HEALER_MESSAGE?.replace("__healed", healedName))
                        healer.addPotionEffect(healerReward)
                        healer.addPotionEffect(bandageRegen)
                        GameData.getPlayer(healer).addPlayerHeals()
                    }
                }
            }
        }
    }

    private fun useBandage(player: Player) {
        if (GameData.getPlayer(player).bleeding) {
            GameData.getPlayer(player).bleeding = false
            player.addPotionEffect(bandageRegen)
            if (player.inventory.itemInMainHand.amount > 1) {
                player.inventory.itemInMainHand.amount--
            } else {
                player.inventory.itemInMainHand.type = Material.AIR
            }
            player.sendMessage(ChatColor.GREEN.toString() + SELF_STOP)
        }
    }
}