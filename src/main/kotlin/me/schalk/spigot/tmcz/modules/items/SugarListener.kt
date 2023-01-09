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

import me.schalk.spigot.lib.config.getMessages
import me.schalk.spigot.lib.config.getSettings
import me.schalk.spigot.lib.math.nextRandomIntBetween
import me.schalk.spigot.tmcz.data.GameData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SugarListener(plugin: JavaPlugin) : ItemModule() {

    init{
        if (IS_ENABLED) {
            SugarListener.plugin = plugin
            val duration = DURATION
            plugin.server.pluginManager.registerEvents(this, plugin)
            plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { processSugarCoolDown() }, 200, (duration * 100) )
            plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { processSugarRandomEffect() }, 200, (duration * 100) )
            plugin.server.logger.info("Sugar system online...")
        }
    }

    companion object {
        private const val SUGAR             = MODULE + ".sugar"
        private val IS_ENABLED              = getSettings().getConfig().getBoolean(SUGAR + ENABLED)
        private val IS_ONLY_IN_GAME         = getSettings().getConfig().getBoolean(SUGAR + IN_GAME_ONLY)
        private val IS_SERVER_WIDE          = getSettings().getConfig().getBoolean(SUGAR + SERVER_WIDE)
        private val DURATION                = getSettings().getConfig().getLong(SUGAR + ".duration")
        private val OVERDOSE                = getSettings().getConfig().getInt(SUGAR + ".overdose")
        private val TOLERANCE               = getSettings().getConfig().getInt(SUGAR + ".tolerance")
        private val TOLERANCE_OVERDOSE      = getSettings().getConfig().getInt(SUGAR + ".tolerance-overdose")
        private val DEATH_CHANCE            = getSettings().getConfig().getInt(SUGAR + ".death-chance")
        private val SIDE_EFFECT_CHANCE      = getSettings().getConfig().getInt(SUGAR + ".side-effect")
        private val LATENT_EFFECT_CHANCE    = getSettings().getConfig().getInt(SUGAR + ".random-effect")

        private val DEATH_MESSAGE           = getMessages().getConfig().getString(SUGAR + ".death")
        private val DEATH_SIDE_EFFECT       = getMessages().getConfig().getString(SUGAR + ".side-effect")
        private lateinit var plugin: JavaPlugin

        private val possibleSideEffects = listOf(
            PotionEffectType.CONFUSION,
            PotionEffectType.BLINDNESS,
            PotionEffectType.DARKNESS,
            PotionEffectType.HARM,
            PotionEffectType.HUNGER,
            PotionEffectType.POISON,
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.WEAKNESS,
            PotionEffectType.WITHER
        )

        fun processSugarCoolDown() {
            plugin.server.onlinePlayers.forEach { player ->
                if (isAllowed(player, IS_SERVER_WIDE, IS_ONLY_IN_GAME)) {
                    val playerBean = GameData.getPlayer(player)
                    if (playerBean.drugged_cooldown > 0) {
                        playerBean.drugged_cooldown--
                    }
                }
            }
        }

        fun processSugarRandomEffect() {
            plugin.server.onlinePlayers.forEach { player ->
                if (isAllowed(player, IS_SERVER_WIDE, IS_ONLY_IN_GAME)) {
                    val playerBean = GameData.getPlayer(player)
                    if (playerBean.drugged_doses > 0 && 1 == nextRandomIntBetween(0, LATENT_EFFECT_CHANCE)) {
                        addSideEffect(player)
                    }
                }
            }
        }

        private fun getSideEffect() : PotionEffect {
            val index = nextRandomIntBetween(0, possibleSideEffects.size)
            val sideEffect = possibleSideEffects[index - 1]
            return PotionEffect(sideEffect, DURATION.toInt() * 20, 1, false, false, false)
        }

        private fun addSideEffect(player: Player) {
            val sideEffect = getSideEffect()
            player.addPotionEffect(sideEffect)
            player.sendMessage(ChatColor.RED.toString() +
                    "Oof, I don't feel so good... is this a side effect of the sugar?")
            GameData.getPlayer(player).side_effect = true
            plugin.server.scheduler.scheduleSyncDelayedTask(plugin,
                { GameData.getPlayer(player).side_effect = false }, (DURATION * 20))
        }
    }

    @EventHandler
    fun takeThatHit(event: PlayerInteractEvent) {
        val player = event.player
        val playerBean = GameData.getPlayer(player)
        if (isAllowed(player, IS_SERVER_WIDE, IS_ONLY_IN_GAME)) {
            val itemInHand = player.inventory.itemInMainHand
            if (itemInHand.type == Material.SUGAR &&
                (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)
            ) {
                event.isCancelled = true
                playerBean.drugged_doses++
                itemInHand.amount--
                applyPotionEffect(player, playerBean)
                showerPlayerWithParticles(player)
                calculateTolerance(playerBean)
                calculateChanceOfSideEffect(player, playerBean)
                calculateChanceOfDeath(player, playerBean)
                checkForOverDose(player, playerBean)
            }
        }
    }

    private fun applyPotionEffect(player: Player, playerBean: GameData.PlayerBean) {
        val potionEffect = PotionEffectType.SPEED
        val currentEffect = player.getPotionEffect(potionEffect)
        val sugarDuration = DURATION.toInt() * 20
        if (currentEffect != null) {
            val newEffect = PotionEffect(potionEffect, currentEffect.duration + sugarDuration, 1, false, false, false)
            player.removePotionEffect(potionEffect)
            player.addPotionEffect(newEffect)
        } else {
            val newEffect = PotionEffect(potionEffect, sugarDuration, 1, false, false, false)
            player.addPotionEffect(newEffect)
        }
        playerBean.drugged_cooldown++
    }

    private fun showerPlayerWithParticles(player: Player) {
        player.spawnParticle(
            Particle.SNOWFLAKE,
            player.location.x, player.location.y.plus(1.75), player.location.z, 10,
            0.05, 0.05, 0.05, 0.005
        )
    }

    private fun calculateTolerance(playerBean: GameData.PlayerBean) {
        if (!playerBean.tolerance && playerBean.drugged_doses > TOLERANCE) {
            playerBean.tolerance = true
        }
    }

    private fun calculateChanceOfSideEffect(player: Player, playerBean: GameData.PlayerBean) {
        if (playerBean.drugged_doses > 0 && 1 == nextRandomIntBetween(0, SIDE_EFFECT_CHANCE)) {
            addSideEffect(player)
        }
    }

    private fun calculateChanceOfDeath(player: Player, playerBean: GameData.PlayerBean) {
        if (playerBean.drugged_doses > 0 && 1 == nextRandomIntBetween(0, DEATH_CHANCE)) {
            player.damage(100.0)
            playerBean.overdose = true
        }
    }

    private fun checkForOverDose(player: Player, playerBean: GameData.PlayerBean) {
        val overdose = if (playerBean.tolerance) TOLERANCE_OVERDOSE else OVERDOSE
        if (playerBean.drugged_cooldown > overdose) {
            player.damage(100.0)
            playerBean.overdose = true
        }
    }

    @EventHandler
    fun sugarDeath(event: PlayerDeathEvent) {
        if (isAllowed(event.entity, IS_SERVER_WIDE, IS_ONLY_IN_GAME)) {
            println(event.entity.lastDamageCause?.cause)
            if (GameData.getPlayer(event.entity).overdose
                && event.entity.lastDamageCause?.cause == EntityDamageEvent.DamageCause.CUSTOM) {
                event.deathMessage = DEATH_MESSAGE?.replace("__player", event.entity.displayName)
            } else if (GameData.getPlayer(event.entity).side_effect) {
                event.deathMessage = DEATH_SIDE_EFFECT?.replace("__player", event.entity.displayName)
            }
            val playerBean = GameData.getPlayer(event.entity)
            playerBean.overdose = false
            playerBean.tolerance = false
            playerBean.drugged_doses = 0
            playerBean.drugged_cooldown = 0
        }
    }
}