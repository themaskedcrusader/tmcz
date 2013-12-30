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

package com.themaskedcrusader.tmcz.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Date;

public class BlockBean {
    private Location location;
    private Material material;
    private Date date;
    private byte data;
    private int despawnSeconds;
    private int respawnSeconds;

    public BlockBean(Block block) {
        this.location = block.getLocation();
        this.material = block.getType();
        this.data = block.getData();
        this.date = new Date();
    }

    public BlockBean(String blockName, int despawnSeconds, int respawnSeconds) {
        this.material = Material.getMaterial(blockName);
        this.despawnSeconds = despawnSeconds;
        this.respawnSeconds = respawnSeconds;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return material;
    }

    public Date getDate() {
        return date;
    }

    public byte getData() {
        return data;
    }

    public int getDespawnSeconds() {
        return despawnSeconds;
    }

    public void setDespawnSeconds(int despawnSeconds) {
        this.despawnSeconds = despawnSeconds;
    }

    public int getRespawnSeconds() {
        return respawnSeconds;
    }

    public void setRespawnSeconds(int respawnSeconds) {
        this.respawnSeconds = respawnSeconds;
    }
}
