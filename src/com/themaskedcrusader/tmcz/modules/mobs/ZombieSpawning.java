package com.themaskedcrusader.tmcz.modules.mobs;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class ZombieSpawning extends Mobs {

    protected ZombieSpawning(final JavaPlugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                ZombieSpawning.spawn();
            }

        }, 30L, Settings.getConfig().getLong(SYSTEM + CYCLE_OFFSET));
    }

    protected static void spawn() {
        World world = WorldUtils.getAuthorizedWorld(plugin, Settings.get());
        Collection<Zombie> zombies = world.getEntitiesByClass(Zombie.class);
        if (zombies.size() < Settings.getConfig().getInt(SYSTEM + ZOMBIES + WORLD_MAX)) {
            Mobs.spawnEntities(EntityType.ZOMBIE, SYSTEM + ZOMBIES + TRIES_PER_CYCLE);
        }
    }

}
