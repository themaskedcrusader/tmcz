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

    protected static final String SYSTEM  = "mob-controls";

    protected static final String ZOMBIES = ".zombies";

    protected Mobs() {}

    public static void initialize(JavaPlugin plugin) {
        Mobs.plugin = plugin;

        if (!Settings.getConfig().getBoolean(SYSTEM + ZOMBIES + ".can-burn")) {
            new ZombieListener(plugin);
        }


    }
}
