package me.schalk.spigot.tmcz

import me.schalk.spigot.lib.config.initMessages
import me.schalk.spigot.lib.config.initSettings
import me.schalk.spigot.lib.javaVersionCompatible
import me.schalk.spigot.tmcz.modules.bleed.BleedModule
import me.schalk.spigot.tmcz.modules.items.BlockListener
import me.schalk.spigot.tmcz.modules.items.ItemModule
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {

    override fun onDisable() {
        super.onDisable()
        BlockListener.cleanUpBlocks(true) // clean up all blocks scheduled in Block Worker
        server.logger.info("TMCz Plugin Unloaded")
    }

    override fun onEnable() {
        if (javaVersionCompatible()) {
            super.onEnable()
            // Initialize Config Files
            initSettings(this)
            initMessages(this)

            BleedModule.initialize(this)
            ItemModule.initialize(this)


            server.logger.info("TMCz Plugin Loaded")
        }
    }
}