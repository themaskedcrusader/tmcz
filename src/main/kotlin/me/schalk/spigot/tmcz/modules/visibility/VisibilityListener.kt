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

package me.schalk.spigot.tmcz.modules.visibility

import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.lib.math.nextRandomIntBetween
import me.schalk.spigot.lib.model.MaskedItem
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.lang.ClassCastException

class VisibilityListener(plugin: JavaPlugin) : VisibilityModule() {

    private val zombieHead = MaskedItem("ZOMBIE_HEAD|1|0")

    init {
        if (getSettings().getConfig().getBoolean(MODULE + ENABLED)) {
            plugin.server.pluginManager.registerEvents(this, plugin)
        }
    }

    @EventHandler
    fun onHostileNotice(event: EntityTargetLivingEntityEvent) {
        try {
            val hostile = event.entity
            val target = event.target
            if (target != null && target is Player && isAllowed(target)) {
                if (hostile is Zombie && target.inventory.helmet!!.type == Material.ZOMBIE_HEAD) {
                    // the zombies think you are one of them. incognito mode
                    event.isCancelled = true
                } else {
                    val noiseLevel = target.exp
                    val distance = hostile.location.distance(target.location)
                    if (distance > 0      && distance < 4  && noiseLevel < (0.15)
                        || (distance > 4  && distance < 7  && noiseLevel < (0.30))
                        || (distance > 7  && distance < 10 && noiseLevel < (0.45))
                        || (distance > 10 && distance < 12 && noiseLevel < (0.60))
                        || (distance > 12 && distance < 15 && noiseLevel < (0.75))
                        || (distance > 15 && noiseLevel < (0.90))
                    ) {
                        event.isCancelled = true
                    }
                }
            }
        } catch (ignored: Exception) {
            // Sometimes the event causes an NPE, let's just ignore those.
        }
    }

    @EventHandler
    fun getZombieHead(event: EntityDeathEvent) {
        if (event.entity.type == EntityType.ZOMBIE) {
            try {
                val entityDeathCause: EntityDamageByEntityEvent =
                    event.entity.lastDamageCause as EntityDamageByEntityEvent
                if (entityDeathCause.damager is Player) {
                    val chance = nextRandomIntBetween(1, 1000)
                    if (chance == 417) { // TODO, make chance configurable?
                        event.drops.add(zombieHead.unmask())
                    }
                }
            } catch (ignored: ClassCastException) { }
        }
    }

    @EventHandler
    fun putZombieHeadOnOnLeftClick(event: PlayerInteractEvent) {
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
                val chance = nextRandomIntBetween(1, 20) // 1 in 20 chance -
                if (chance == 7) {
                    player.inventory.helmet = null
                }
            }
        }
    }

}