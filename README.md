# TMCz Zombie Survival

This is a Spigot/Bukkit plugin that provides nearly all the functionality as provided by the MINEz Game Mode developed by ShotBow. I wanted to be able to host my own server with my own map, but since ShotBow doesn't publish their own plugin, I decided to write my own. This provides nearly every game mechanic as the official MineZ Servers, allowing you to host the game on your own system. All software was written by me and all features, though inspired by ShotBow, are my own design and coded using the Spigot/Bukkit API.

This software is licensed under the MIT license, as published in License.txt. Please feel free to do with it what you will, and if you think it needs new features, you can either request them or build them yourself. Spigot is a pretty simple API to learn.

This software is written 100% in Kotlin. This plugin **requires** the tmc-lib plugin, which provides required functionality for many of these features as well as the kotlin wrapper packages required to load kotlin-based code from within Spigot and Minecraft, which are both Java based applications.

The original version of this plugin was written in Java using the standard Bukkit API, but in an effort to learn a new programming language, all features have been ported to Kotlin. The Kotlin programming language is based on Java, andKotlin provides 100% compatibility with Java APIs. Kotlin does compile to native Java Bytecode, but it does require certain libraries for that bytecode to be run alongside any pure-Java application, Those required Kotlin wrapper libraries are bundled with the TMC Lib plugin. Kotlin is quickly becoming the default language for Android, so experimenting with the language in a safe environment (such as a Bukkit plugin) will provide you with exposure to many of the language features. So please feel free to fork and dabble with this source code.
  
----  
# Built-in Game Modules:
## Visibility Module
#### Status: `Code Complete`
The visibility system enhances the built-in visibility system within Minecraft. In Vanilla Minecraft, Mobs will target the player when they are within 16 blocks. Crouching makes the player quieter and mobs will only target the player if they are within 12 blocks. Vanilla Minecraft has only 2 layers in their visibility system. In TMCz, there are 7 layers of visibility which are visually depicted on the screen in the experience bar. Crouching and standing still is quieter than crouching and walking. Sprinting is quieter than sprinting and jumping. And all actions are slightly quieter during thunderstorms. This allows for a wider range of gameplay as mobs ability to target the player depend on how quiet the player is being.

## Thirst Module
#### Status: `Code Complete`
The Thirst System introduces gameplay where the player will continually get thirsty and will have to drink water in order to stay hydrated. The Level number is the player's Hydration level. When it hits zero, the player begins to take thirst damage, which can lead to death.

## Infection Module
#### Status: `Planning`
The infection system introduces gameplay mechanic where damage from a zombie has a chance to infect the player. An infected player will take damage as the infection spreads until it leads to death. The player or another player can administer antibiotics which will cure the infection. However, the longer the player has been infected, the more antibiotic will need to be applied to kill the infection. The player or another player can administer antibiotic. If a player chooses to use their resources to heal another, they are rewarded with a regeneration effect for a configurable amount of time

## Bleed Module
#### Status: `Nearing Completion`
The Bleed System introduces gameplay where getting hurt by various types of damage causes you to bleed. A bleeding player continually takes bleeding damage, accompanied by bloody heart particles. Bleeding can be stopped by applying a bandage.  The player can apply their own bandage, or another player can apply a bandage to the bleeding player. If a player chooses to use their resources to bandage another, they are rewarded with a regeneration effect for a configurable amount of time. The player can bleed out and die if the bleeding is not stopped before they lose all of their hearts.

## Health Module
#### Status: `Planning`
The health system controls how many hearts of health a player has. By default, the player has 20 points of health, or 10 hearts. However, that can be modified in this module. You can configure your server to limit the number of hearts a player can have.

## Custom Item Module
#### Status: `In Dev`
The custom items module controls how players interact with the world. When enabled, it will protect the world from player damage and griefing, similar to WorldGuard. Breaking and placing of blocks will be prohibited on the configured world. Only players who are a server operator or have the permission `tmcz.builder` will be allowed to build or change the world without prohibitions by this module.

This module tracks all world interactions based on a few modifiable items. These items replicate gameplay as implemented by ShotBow in MineZ:
* **Ender Pearls** - In TMCz, ender pearls can be configured to act as hand grenades. When thrown they will detonate upon hitting the ground or another mob. The detonations can also be configured to protect or destroy world blocks.
* **Melons** - In TMCz, Melons are a great source of food. They can only be harvested with an approved tool, though. Since melons grow from a stalk, breaking a melon won't stop the growth of another melon, so feel free to take those melons for food and rest assured that they'll grow back for the next player who comes along.
* **Mushrooms** - In TMCz, Mushrooms can be harvested to make food. Harvested mushrooms will eventually regrow from the mycelium that lives under the soil, so you can rest assured that eventually another mushroom will grow.
* **Cobwebs** - in TMCz, Cobwebs can hedge up the way, but can be broken with an approved tool. However, spiders are known to rebuild webs that have been broken. The player can place harvested webs to hedge up the way of others, but placed webs will disintegrate after a little while.
* **Sugar** - In TMCz, sugar is the drug of choice. Dusting yourself with sugar will give you a speed boost allowing you to run away from zombies and other players, but overuse may cause certain side effect symptoms to randomly appear. There is also a chance to overdose which can lead to muscle paralysis or death.
* **Other Blocks** - Any available Minecraft block can be configured in this module to allow players to break and/or place them in the world. Each configuration also allows for placed blocks to despawn as well as broken blocks to respawn as configured. This gives server operators the ability to allow players to modify the world without permanently ruining the map they've built.

## Stack Limiter Module
#### Status: `planning`
While having deep pockets in Minecraft is always a nice thing, this module allows server operators to limit stacks of items in their TMCz worlds. However, the nature of how stacks are limited means that this configuration applies *server wide*! If you run a server with multiple worlds, and the Zombie world  is only one of many worlds on your server, stack limits will apply to your non Zombie Survival worlds as well. If you want to enabled stack limits only in the Zombie Survival game, you'll have to set up a dedicated server for TMCz and setup cross-server portals to transport your players back and forth. This is how the lobby of the ShotBow Network works.

## Mob Control Module
#### Status: `Code Complete for Zombies, Planning for all others`
This is a Zombie Survival Game, and as such, mobs need to have certain controls. For instance, Zombies can be configured to not burn in the sun, drop zombie heads upon death (rare), and a player wearing a zombie head can prevent being targeted by zombies by going incognito.

All other Minecraft mobs can be configured to either follow their normal spawning algorithms or to be prohibited from spawning in the world. For instance, you can configure all hostile mobs *except* Zombies to not spawn, but allow passive mobs to spawn. This will allow for animals to exist in your world and provide a food source for your players.

## Auto-save Module
#### Status: `Planning`
The auto-save system, when enabled, will continually save the current game state of all players to disk, and when the server starts up will load the saved states into memory so that when players reconnect, they'll start when and where they previously were.

## Game Control Module
#### Status: `In Dev`
This is the bread and butter of TMCz. This module allows you to configure the game. Players can automatically spawn into the game or a lobby can be configured where players can choose when to start the game by using the `tmcz` or `minez` command. You can configure PvP, and implement a point system where players can choose to be healers to help their fellow player or bandits who kill and steal from other players.

You can configure different spawn zones which will be chosen at random or static zones where players will always spawn. You can configure a start kit which will arm players with necessary items when they start the game

