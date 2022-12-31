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

// TODO: THIS IS FOR TESTING ONLY - REMOVE BEFORE PACKAGING

package me.schalk.spigot.tmcz.test

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class ClientTestListener(plugin: JavaPlugin) : Listener {
    private val parent: JavaPlugin

    init {
        parent = plugin
        parent.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun clientListener(event: PlayerInteractEvent) {
//        val player = event.player
//        println("PLAYER: $player")
//        println("ACTION: " + event.action.name)
//        if (event.action.name == "RIGHT_CLICK_BLOCK") {
//            val block = event.clickedBlock
//            println("Block: " + block)
//            println("Shukler Check : " + (block?.state is ShulkerBox))
//            val smartChest = block?.let { getSmartChest(it) }
//            if (smartChest != null) {
//                    println("Is Single Chest : " + smartChest.type)
//                    println("Left Location   : " + smartChest.location)
//                    println("Right Location  : " + smartChest.otherSideLocation)
//                }
//            }
//        // How to read an object, and create a new object with damage
////        val item2 = ItemStack(Material.WOODEN_AXE, 1)
////        val item2Meta: Damageable = item2.itemMeta as Damageable
////        item2Meta.damage = 25
////        item2.itemMeta = item2Meta
////        player.inventory.addItem(item2)
//
//        // How to read a potion and how to add a new potion to inventory
////        val item3 = Material.matchMaterial("potion".uppercase())?.let { ItemStack(it) }
////        val item3Meta : PotionMeta = item3.itemMeta as PotionMeta
////        val potionData = PotionData(PotionType.FIRE_RESISTANCE, false, false)
////        item3Meta.basePotionData = potionData
////        item3.itemMeta = item3Meta
////        player.inventory.addItem(item3)
//
//        val itemInHand = player.inventory.itemInMainHand
//
//
//        val item= MaskedItem(player.inventory.itemInMainHand)
//        parent.server.logger.info(item.serialize())
//
//        if (getChancePercentage() < 50) {
//            val defaultItem = MaskedItem.default()
//
//            val enchantedItemString = "NETHERITE_SWORD|1|154|||||A Fine Generated Sword|Heady Loppy|{minecraft:sweeping,10\\minecraft:bane_of_arthropods,5\\minecraft:protection,4\\minecraft:fire_aspect,1\\}"
//            val headyLoppy = MaskedItem(enchantedItemString)
//
////            val axe = MaskedItem("NETHERITE_AXE|1|124||||A fine generated axe|Choppy Chippy")
////            val potion1 = MaskedItem("POTION|1|0|LUCK|FALSE|FALSE|Not so lucky, eh|Luck -1")
////            val potion2 = MaskedItem("LINGERING_POTION|1|0|REGEN|TRUE|FALSE|Better than nothing|Nothing +1")
////            val potion3 = MaskedItem("SPLASH_POTION|1|0|INSTANT_DAMAGE|FALSE|TRUE|OWIE|Hurtz Donut")
////            val myHead = MaskedItem("PLAYER_HEAD|1|0|||||Meeeee|Masked_Crusader")
//
//            player.inventory.addItem(
//                headyLoppy.unmask()
//            )
//        }

//        var pause = true;
    }

}