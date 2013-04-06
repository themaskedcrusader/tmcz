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

package com.themaskedcrusader.tmcz.modules.healer;

import com.themaskedcrusader.bukkit.chest.MaskedItem;
import com.themaskedcrusader.bukkit.config.Messages;
import com.themaskedcrusader.bukkit.config.Settings;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.tmcz.data.PlayerUtil;
import com.themaskedcrusader.tmcz.data.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class HealerListener implements Listener {
    private static JavaPlugin plugin;

    public HealerListener(JavaPlugin plugin) {
        HealerListener.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void allOpsAreGod(EntityDamageEvent event) {
        if (Settings.getConfig().getBoolean("world.op-is-god") && event.getEntity().getType() == EntityType.PLAYER &&
                ((Player) event.getEntity()).isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void startProcess(EntityDamageByEntityEvent event) {
        if (isAllowed(event) && ((Player) event.getDamager()).getItemInHand().getType() == Material.PAPER) {
            Player healed = (Player) event.getEntity();
            Player healer = (Player) event.getDamager();
            PlayerUtil.setStatus(healed, Status.BANDAGED, true);
            sendStartMessage(healer, healed);

            if (healer.getItemInHand().getAmount() == 1) {
                healer.setItemInHand(new ItemStack(Material.AIR));
            } else {
                healer.getItemInHand().setAmount(healer.getItemInHand().getAmount() - 1);
            }

            clearBandaging(healed);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void applyOintment(EntityDamageByEntityEvent event) {
        if (isAllowed(event)) {
            String itemCode = Settings.getConfig().getString(Healer.OINTMENT);
            ItemStack codedItem = new MaskedItem(itemCode).unmask();
            ItemStack healerItem = ((Player) event.getDamager()).getItemInHand();
            if (codedItem.getType() == healerItem.getType() && codedItem.getData().getData() == healerItem.getData().getData()) {
                Player healed = (Player) event.getEntity();
                Player healer = (Player) event.getDamager();

                if (PlayerUtil.isStatusSet(healed, Status.BANDAGED)) {
                    PlayerUtil.setStatus(healed, Status.OINTMENT, true);
                    sendOintmentMessage(healer, healed);

                    if (healerItem.getAmount() == 1) {
                        healer.setItemInHand(new ItemStack(Material.AIR));
                    } else {
                        healerItem.setAmount(healerItem.getAmount() - 1);
                    }

                } else {
                    String message = parseMessage(Messages.getConfig().getString(Healer.HEALER_FAIL_1), healer, healed);
                    healer.sendMessage(message);
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void applyAntibiotic(EntityDamageByEntityEvent event) {
        if (isAllowed(event)) {
            String itemCode = Settings.getConfig().getString(Healer.ANTIBIOTIC);
            ItemStack codedItem = new MaskedItem(itemCode).unmask();
            ItemStack healerItem = ((Player) event.getDamager()).getItemInHand();
            if (codedItem.getType() == healerItem.getType() && codedItem.getData().getData() == healerItem.getData().getData()) {
                Player healed = (Player) event.getEntity();
                Player healer = (Player) event.getDamager();

                if (PlayerUtil.isStatusSet(healed, Status.BANDAGED)) {
                    PlayerUtil.setStatus(healed, Status.ANTIBIOTIC, true);
                    sendAntibioticMessage(healer, healed);

                    if (healerItem.getAmount() == 1) {
                        healer.setItemInHand(new ItemStack(Material.AIR));
                    } else {
                        healerItem.setAmount(healerItem.getAmount() - 1);
                    }

                } else {
                    String message = Messages.getConfig().getString(Healer.HEALER_FAIL_1);
                    message = message.replace(Healer.HEALED, ChatColor.YELLOW + healed.getName() + ChatColor.RED);
                    healer.sendMessage(message);
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public static void sealTheDeal(EntityDamageByEntityEvent event) {
        if (isAllowed(event) && ((Player) event.getDamager()).getItemInHand().getType() == Material.SHEARS) {
            Player healed = (Player) event.getEntity();
            Player healer = (Player) event.getDamager();

            if (PlayerUtil.isStatusSet(healed, Status.BANDAGED)) {
                PlayerUtil.setStatus(healed, Status.BANDAGED, false);
                int regen = 100;

                if (PlayerUtil.isStatusSet(healed, Status.OINTMENT)) {
                    PlayerUtil.setStatus(healed, Status.BLEEDING, false);
                    regen += 25;
                }

                if (PlayerUtil.isStatusSet(healed, Status.ANTIBIOTIC)) {
                    PlayerUtil.setStatus(healed, Status.INFECTED, false);
                    regen += 25;
                }

                PotionEffect healedEffect = new PotionEffect(PotionEffectType.REGENERATION, regen, 0);
                PotionEffect healerEffect = new PotionEffect(PotionEffectType.REGENERATION, 50, 0);
                healed.addPotionEffect(healedEffect);
                healer.addPotionEffect(healerEffect);
                if (PlayerUtil.isPlaying(healer)) {
                    PlayerUtil.addPlayerHeal(healer, Settings.get());
                }
                sendHealedMessage(healer, healed);
                healer.getItemInHand().setDurability((short) (healer.getItemInHand().getDurability() + Short.parseShort("30")));
                if (healer.getItemInHand().getDurability() > 238) {
                    healer.setItemInHand(new ItemStack(Material.AIR));
                }

            } else {
                String message = parseMessage(Messages.getConfig().getString(Healer.HEALER_FAIL_2), healer, healed);
                healer.sendMessage(message);
            }

            event.setCancelled(true);
        }
    }

    private static boolean isAllowed(EntityDamageByEntityEvent event) {
        return WorldUtils.isAllowed(event.getEntity().getWorld(), Settings.get()) &&
                event.getEntity().getType() == EntityType.PLAYER &&
                event.getDamager().getType() == EntityType.PLAYER;
    }

    private static void clearBandaging(final Player player) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public  void run() {
                if (PlayerUtil.isStatusSet(player, Status.BANDAGED)) {
                    clearHealingStatus(player);
                    player.sendMessage(ChatColor.RED + Messages.getConfig().getString(Healer.HEALED_OOT));
                }
            }
        }, 600L);
    }

    private static void clearHealingStatus(Player player) {
        PlayerUtil.setStatus(player, Status.BANDAGED, false);
        PlayerUtil.setStatus(player, Status.ANTIBIOTIC, false);
        PlayerUtil.setStatus(player, Status.OINTMENT, false);
    }

    private static void sendStartMessage(Player healer, Player healed) {
        String healedMsg = parseMessage(Messages.getConfig().getString(Healer.HEALED_START), healer, healed);
        String healerMsg = parseMessage(Messages.getConfig().getString(Healer.HEALER_START), healer, healed);
        healed.sendMessage(healedMsg);
        healer.sendMessage(healerMsg);
    }

    private static void sendOintmentMessage(Player healer, Player healed) {
        String healedMsg = parseMessage(Messages.getConfig().getString(Healer.HEALED_OINTMENT), healer, healed);
        String healerMsg = parseMessage(Messages.getConfig().getString(Healer.HEALER_OINTMENT), healer, healed);
        healed.sendMessage(healedMsg);
        healer.sendMessage(healerMsg);
    }

    private static void sendAntibioticMessage(Player healer, Player healed) {
        String healedMsg = parseMessage(Messages.getConfig().getString(Healer.HEALED_ANTIBIOTIC), healer, healed);
        String healerMsg = parseMessage(Messages.getConfig().getString(Healer.HEALER_ANTIBIOTIC), healer, healed);
        healed.sendMessage(healedMsg);
        healer.sendMessage(healerMsg);
    }

    private static void sendHealedMessage(Player healer, Player healed) {
        String healedMsg = parseMessage(Messages.getConfig().getString(Healer.HEALED_FINISH), healer, healed);
        String healerMsg = parseMessage(Messages.getConfig().getString(Healer.HEALER_FINISH), healer, healed);
        healed.sendMessage(healedMsg);
        healer.sendMessage(healerMsg);
    }

    private static String parseMessage(String original, Player healer, Player healed) {
        String
        toReturn = original.replace(Healer.HEALED, ChatColor.YELLOW + healed.getName() + ChatColor.RED);
        toReturn = toReturn.replace(Healer.HEALER, ChatColor.AQUA + healer.getName() + ChatColor.RED);
        return toReturn;
    }
}
