package com.themaskedcrusader.tmcz.modules.mobs;

import com.themaskedcrusader.bukkit.config.ConfigAccessor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnControl {
    private static String fileName = "spawn-control.yml";
    private static ConfigAccessor settings;

    private SpawnControl() {}

    public static void init(JavaPlugin plugin) {
        settings = new ConfigAccessor(plugin, fileName);
        settings.reloadConfig();
        settings.saveDefaultConfig();
    }

    public static FileConfiguration getConfig() {
        return settings.getConfig();
    }

    public static ConfigAccessor get() {
        return settings;
    }

    public static void saveConfig() {
        settings.saveConfig();
    }
}
