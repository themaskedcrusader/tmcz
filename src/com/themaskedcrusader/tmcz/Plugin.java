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

package com.themaskedcrusader.tmcz;

import com.themaskedcrusader.bukkit.Library;
import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.tmcz.command.CommandProcessor;
import com.themaskedcrusader.tmcz.command.GameCommands;
import com.themaskedcrusader.tmcz.modules.DefaultListener;
import com.themaskedcrusader.tmcz.modules.autosave.AutoSave;
import com.themaskedcrusader.tmcz.modules.bleed.Bleed;
import com.themaskedcrusader.tmcz.modules.game.Game;
import com.themaskedcrusader.tmcz.modules.healer.Healer;
import com.themaskedcrusader.tmcz.modules.health.Health;
import com.themaskedcrusader.tmcz.modules.infection.Infection;
import com.themaskedcrusader.tmcz.modules.items.BlockWorker;
import com.themaskedcrusader.tmcz.modules.items.Items;
import com.themaskedcrusader.tmcz.modules.mobs.Mobs;
import com.themaskedcrusader.tmcz.modules.mobs.Spawning;
import com.themaskedcrusader.tmcz.modules.thirst.Thirst;
import com.themaskedcrusader.tmcz.modules.visibility.Visibility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Plugin extends JavaPlugin {

    public void onEnable() {
        try {
            loadConfiguration();
            new DefaultListener(this);
            AutoSave.initialize(this);
            Bleed.initialize(this);
            Game.initialize(this);
            Healer.initialize(this);
            Health.initialize(this);
            Infection.initialize(this);
            Items.initialize(this);
    //        Stack.initialize(this);
            Thirst.initialize(this);
            Visibility.initialize(this);
            Mobs.initialize(this);
            Spawning.initialize(this);

            AutoSave.reloadAllPlayers(this);
            this.getLogger().info("Plugin Plugin Activated");

        } catch (NoClassDefFoundError e) {
            getLogger().log(Level.SEVERE,  "TMC-LIB Library Missing or cannot load: Disabling Plugin.");
            getLogger().log(Level.SEVERE,  "See install instructions at http://dev.bukkit.org/server-mods/tmc-lib/");
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    public void onDisable() {
        try {
            AutoSave.saveAllPlayers(this);
            BlockWorker.cleanUpBlocks(true);
        } catch (Error ignored) {

        }
    }

    private void loadConfiguration() {
        Library.checkForNewVersion(getServer().getConsoleSender());
        Settings.init(this);
        Messages.init(this);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CommandProcessor cp = new CommandProcessor(this);
        if (s.equalsIgnoreCase("tmcz")) {
            cp.processCommand(commandSender, strings);
        } else {
            try {
                Player player = (Player) commandSender;

                if (s.equalsIgnoreCase("spawnz")) {
                    GameCommands.startGame(player);
                } else if (s.equalsIgnoreCase("killz")) {
                    GameCommands.playerStats(player);
                } else if (s.equalsIgnoreCase("diez")) {
                    GameCommands.killPlayer(player);
                }

            } catch (Exception e) {
                commandSender.sendMessage(ChatColor.RED + "The command entered can only be used in-game, not from console");
            }
        }
        return true;
    }
}
