package com.themaskedcrusader.tmcz.modules.mobs;

import com.themaskedcrusader.bukkit.config.Settings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class ZombieListener extends Mobs implements Listener {
    JavaPlugin plugin;

    public ZombieListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void cancelZombieFire(EntityCombustEvent event) {
        if (!Settings.getConfig().getBoolean(SYSTEM + ZOMBIES + ".can-burn")) {
            EntityType et = event.getEntityType();
            event.setCancelled(et == EntityType.ZOMBIE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void allowZombieFireByPlayer(EntityCombustByEntityEvent event) {
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void allowZombieFireByBlock(EntityCombustByBlockEvent event) {
        event.setCancelled(false);
    }

}
