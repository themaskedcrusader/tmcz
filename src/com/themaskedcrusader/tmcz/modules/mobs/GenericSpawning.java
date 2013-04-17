/*
 * Copyright 2013 Topher Donovan (themaskedcrusader.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.themaskedcrusader.tmcz.modules.mobs;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class GenericSpawning extends Spawning implements Listener {
    String className = "org.bukkit.entity";
    EntityType creature;
    private boolean allowedToSpawn = true;

    protected GenericSpawning(JavaPlugin plugin, EntityType et) {
        this.creature  = et;
        allowedToSpawn = SpawnControl.getConfig().getBoolean(SPAWN_CONTROL + et.getName().toLowerCase());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        if (allowedToSpawn &&
                SpawnControl.getConfig().getBoolean(SYSTEM +  et.getName().toLowerCase() + ENABLED)) {
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

                @Override
                public void run() {
                    spawn(creature);
                }

            }, 30L, Settings.getConfig().getLong(SYSTEM + CYCLE_OFFSET));
        }
    }

    private void spawn(EntityType creature) {
        try {
            Class clazz = Class.forName(className + "." + creature.getName());
            World world = WorldUtils.getAuthorizedWorld(plugin, Settings.get());
            Collection livingCreatures = world.getEntitiesByClass(clazz);
            if (livingCreatures.size() < SpawnControl.getConfig().getInt(SYSTEM + creature.getName().toLowerCase() + Spawning.WORLD_MAX)) {
                Spawning.spawnEntities(EntityType.ZOMBIE, SYSTEM + creature.getName().toLowerCase() + TRIES_PER_CYCLE);
            }
        } catch (Exception ignored) {        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void allowCreatureSpawn(CreatureSpawnEvent event) {
        boolean isAllowed = WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get());
        if (event.getEntity().getType() == creature && isAllowed) {
            if (!allowedToSpawn) {
                event.setCancelled(true);

            } else if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM &&
                !SpawnControl.getConfig().getBoolean(SYSTEM + creature.toString().toLowerCase() + VANILLA_SPAWNS)) {
                event.setCancelled(true);
            }
        }
    }
}
