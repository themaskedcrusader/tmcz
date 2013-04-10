package com.themaskedcrusader.tmcz.modules.mobs;

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Mobs {
    protected static JavaPlugin plugin;
    protected static Random random = new Random();

    protected static final String SYSTEM           = "mob-controls";
    protected static final String CYCLE_OFFSET     = ".cycle-offset";
    protected static final String ENABLED          = ".spawn-enabled";
    protected static final String WORLD_MAX        = ".world-max";
    protected static final String TRIES_PER_CYCLE  = ".tries-per-cycle";

    protected static final String ZOMBIES = ".zombies";
    protected static final String GIANTS  = ".giants";
    protected static final String WOLVES  = ".wolves";

    protected Mobs(){}

    public static void initialize(JavaPlugin plugin) {
        Mobs.plugin = plugin;
        if (Settings.getConfig().getBoolean(SYSTEM + ZOMBIES + ENABLED)) {
            new ZombieSpawning(plugin);
            new ZombieListener(plugin);
        }
    }

    protected static void spawnEntities(EntityType entityType, String numberPerCycle) {
        int numberToTryToSpawn = Settings.getConfig().getInt(numberPerCycle);
        for (int i = 0; i < numberToTryToSpawn ; i++) {
            World world = WorldUtils.getAuthorizedWorld(plugin, Settings.get());
            Chunk chunk = getRandomLoadedChunk(world.getLoadedChunks());
            int rx = (chunk.getX() * 16) + random.nextIntBetween(0, 15);
            int ry = random.nextIntBetween(0, 256);
            int rz = (chunk.getZ() * 16) + random.nextIntBetween(0, 15);
            Block block = getValidBlock(world, rx, ry, rz);
            if (block != null && WorldUtils.getNearbyPlayers(block.getLocation(), 24).size() ==  0) {
                LivingEntity entity = (LivingEntity) world.spawnEntity(block.getLocation(),  entityType);
                CreatureSpawnEvent event = new CreatureSpawnEvent(entity,  CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
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
