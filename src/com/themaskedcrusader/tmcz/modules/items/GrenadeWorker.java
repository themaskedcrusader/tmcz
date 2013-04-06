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

package com.themaskedcrusader.tmcz.modules.items;

import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GrenadeWorker extends Items implements Listener {

    private static final String GRENADE  = ".end-grenade";
    public static final String _ENABLED  = SYSTEM + GRENADE + ENABLED;
    public static final String _IN_GAME  = SYSTEM + GRENADE + IN_GAME;
    public static final String _SERVER   = SYSTEM + GRENADE + SERVER_WIDE;
    public static final String _PROTECT  = SYSTEM + GRENADE + ".protect-world";
    public static final String _RADIUS   = SYSTEM + GRENADE + ".radius";

    public GrenadeWorker(JavaPlugin plugin) {
        if (Settings.getConfig().getBoolean(_ENABLED)) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void enderPearlGrenade(PlayerTeleportEvent event) {
        if (allowGrenade(event.getPlayer(), event.getCause())) {
            event.setCancelled(true);
            Location toExplode = event.getTo();
            toExplode.getWorld().createExplosion(toExplode, (float) Settings.getConfig().getDouble(_RADIUS));
        }
    }

    public static boolean allowGrenade(Player player, PlayerTeleportEvent.TeleportCause cause) {
        return isAllowed(player, _SERVER, _IN_GAME) &&  cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL;
    }

    @EventHandler
    public void cancelExplosion(EntityExplodeEvent event) {
        if (WorldUtils.isAllowed(event.getLocation().getWorld(), Settings.get()) &&
                Settings.getConfig().getBoolean(_PROTECT)) {
            event.blockList().clear();
        }
    }
}
