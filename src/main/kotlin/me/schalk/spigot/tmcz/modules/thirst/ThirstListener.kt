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

package me.schalk.spigot.tmcz.modules.thirst

import me.schalk.spigot.lib.math.nextRandomIntBetween
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class ThirstListener(val plugin: JavaPlugin) : ThirstModule() {

    init{
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun setThirstLevelOnSpawn(event: PlayerRespawnEvent) {
        val player = event.player
        if (isAllowed(player)) {
            player.level = START
            GameData.getPlayer(player).thirsty = false
        }
    }

    @EventHandler
    fun playerDrinkEvent(event: PlayerItemConsumeEvent) {
        if (isAllowed(event.player) && event.item.type == Material.POTION) {
            quenchThatThirst(event.player, event.item)
            chanceOfBrokenBottle(event.player)
        }
    }

    private fun quenchThatThirst(player: Player, potion: ItemStack) {
        GameData.getPlayer(player).thirsty = false
        var restored = REFILL_POTION
        if ((potion.itemMeta as PotionMeta).basePotionData.type == PotionType.WATER) {
            restored = REFILL_WATER
        }
        player.level = (player.level + restored).coerceAtMost(FULL)
        player.removePotionEffect(PotionEffectType.CONFUSION)
        player.sendMessage(REFILL_MESSAGE)
    }

    private fun chanceOfBrokenBottle(player: Player) {
        val breakChance = nextRandomIntBetween(0, 100)
        if (BREAK_CHANCE > breakChance) {
            player.sendMessage("Dang, my bottle broke")
            val item = player.inventory.itemInMainHand
            player.inventory.remove(item)
        }
    }

    @EventHandler
    fun preventExpDropOnAllMobDeath(event: EntityDeathEvent) {
        if (isAllowed(event.entity)) {
            event.droppedExp = 0
        }
    }

    @EventHandler
    fun handleThirstyDeath(event: PlayerDeathEvent) {
        if (isAllowed(event.entity)
            && GameData.getPlayer(event.entity).thirsty
            && event.entity.lastDamageCause?.cause == EntityDamageEvent.DamageCause.CUSTOM)
        {
            event.deathMessage = DEATH_MESSAGE?.replace("__player", event.entity.displayName)
        }
        event.entity.removePotionEffect(PotionEffectType.CONFUSION)
        GameData.getPlayer(event.entity).thirsty = false
    }
}