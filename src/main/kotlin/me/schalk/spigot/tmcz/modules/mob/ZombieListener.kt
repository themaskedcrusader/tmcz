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
import me.schalk.spigot.lib.math.nextRandomIntBetween
import me.schalk.spigot.lib.model.MaskedItem
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class ZombieListener(val plugin: JavaPlugin) : MobModule() {

    companion object {
        private val zombieHead = MaskedItem("ZOMBIE_HEAD|1|0")
        private val ZOMBIE = ".zombie"
        private val ENABLED = getSettings().getConfig().getBoolean(MODULE + ZOMBIE + ".enabled")
        private val CAN_BURN = getSettings().getConfig().getBoolean(MODULE + ZOMBIE + ".can-burn")
        private val HEAD_HIDES_PLAYER = getSettings().getConfig().getBoolean(MODULE + ZOMBIE + ".head-hides")
        private val HEAD_DROP_CHANCE = getSettings().getConfig().getInt(MODULE + ZOMBIE + ".head-drop-chance")
        private val HEAD_BREAK_CHANCE = getSettings().getConfig().getInt(MODULE + ZOMBIE + ".head-break-chance")
    }

    init{
        if (ENABLED) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }
    }

    @EventHandler
    fun cancelZombieFire(event: EntityCombustEvent) {
        if (!CAN_BURN) {
            event.isCancelled = event.entityType == EntityType.ZOMBIE
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun allowZombieFireByPlayer(event: EntityCombustByEntityEvent) {
        event.isCancelled = false
    }

    @EventHandler(ignoreCancelled = true)
    fun allowZombieFireByBlock(event: EntityCombustByBlockEvent) {
        event.isCancelled = false
    }

    @EventHandler
    fun getZombieHead(event: EntityDeathEvent) {
        if (event.entity.type == EntityType.ZOMBIE && event.entity.lastDamageCause is EntityDamageByEntityEvent) {
            val entityDeathCause = event.entity.lastDamageCause as EntityDamageByEntityEvent
            if (entityDeathCause.damager is Player) {
                if (1 == nextRandomIntBetween(0, HEAD_DROP_CHANCE)) {
                    event.drops.add(zombieHead.unmask())
                }
            }
        }
    }

    @EventHandler
    fun putOnZombieHead(event: PlayerInteractEvent) {
        val player = event.player
        val itemInMainHand = player.inventory.itemInMainHand
        if (itemInMainHand.type == Material.ZOMBIE_HEAD && player.inventory.helmet == null ) {
            itemInMainHand.amount--
            event.player.inventory.helmet = zombieHead.unmask()
            event.isCancelled = true
        }
    }

    @EventHandler
    fun wornZombieHeadRandomlyBreaks(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (player.inventory.helmet != null && player.inventory.helmet!!.type == Material.ZOMBIE_HEAD) {
                if (1 == nextRandomIntBetween(0, HEAD_BREAK_CHANCE)) {
                    player.inventory.helmet = null
                    player.playSound(player.location, Sound.ENTITY_GOAT_HORN_BREAK, 1.0f, 1.0f)
                    player.spawnParticle(Particle.CLOUD, player.location.x, player.location.y + 2, player.location.z,
                        3, 0.0, 0.0, 0.0, 0.01)
                }
            }
        }
    }

    @EventHandler
    fun zombieHeadHides(event: EntityTargetLivingEntityEvent) {
        if (HEAD_HIDES_PLAYER) {
            val hostile = event.entity
            val target = event.target
            if (hostile is Zombie && target is Player && target.inventory.helmet!!.type == Material.ZOMBIE_HEAD) {
                event.isCancelled = true
            }
        }
    }
}