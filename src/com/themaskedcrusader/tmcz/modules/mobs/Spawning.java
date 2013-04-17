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

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Spawning {
    protected  static JavaPlugin plugin;
    private static Random random = new Random();

    protected static final String SPAWN_CONTROL    = "spawn-control.";

    protected static final String SYSTEM           = "custom-spawn-control.";
    protected static final String CYCLE_OFFSET     = "cycle-offset";
    protected static final String ENABLED          = ".enabled";
    protected static final String WORLD_MAX        = ".world-max";
    protected static final String TRIES_PER_CYCLE  = ".tries-per-cycle";
    protected static final String VANILLA_SPAWNS   = ".vanilla-spawns";

    protected Spawning() {}

    public static void initialize(JavaPlugin plugin) {
        Spawning.plugin = plugin;
        SpawnControl.init(plugin);

        for(EntityType et : EntityType.values()) {
            if (et.isAlive() && et != EntityType.PLAYER) {
                new GenericSpawning(plugin, et);
            }
        }
    }

    // #########################################################
    // #####   TMC's Custom Daylight Spawning Algorithm    #####
    // #########################################################

    protected static void spawnEntities(EntityType entityType, String numberPerCycle) {
        int numberToTryToSpawn = SpawnControl.getConfig().getInt(numberPerCycle);
        for (int i = 0; i < numberToTryToSpawn ; i++) {
            World world = WorldUtils.getAuthorizedWorld(plugin, Settings.get());
            Chunk chunk = getRandomLoadedChunk(world.getLoadedChunks());
            int rx = (chunk.getX() * 16) + random.nextIntBetween(0, 15);
            int ry = random.nextIntBetween(0, 256);
            int rz = (chunk.getZ() * 16) + random.nextIntBetween(0, 15);
            Block block = getValidBlock(world, rx, ry, rz);
            if (block != null && WorldUtils.getNearbyPlayers(block.getLocation(), 24).size() ==  0) {
                LivingEntity entity = (LivingEntity) world.spawnEntity(block.getLocation(),  entityType);
                CreatureSpawnEvent event = new CreatureSpawnEvent(entity,  CreatureSpawnEvent.SpawnReason.CUSTOM);
                plugin.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    private static Block getValidBlock(World world, int x, int y, int z) {
        Block mid = world.getBlockAt(x, y, z);
        if (y > 64 && mid.getType() == Material.AIR) {
            mid = world.getHighestBlockAt(x, z);
            y = mid.getY();
        }
        Block floor = world.getBlockAt(x, y - 1, z);
        Block head  = world.getBlockAt(x, y + 1, z);

        return (floor.getType().isSolid() && !mid.getType().isSolid() && !head.getType().isSolid()) ? head : null;
    }

    private static Chunk getRandomLoadedChunk(Chunk[] loadedChunks) {
        int randomChunkId = random.nextIntBetween(0, loadedChunks.length - 1);
        return loadedChunks[randomChunkId];
    }

}
