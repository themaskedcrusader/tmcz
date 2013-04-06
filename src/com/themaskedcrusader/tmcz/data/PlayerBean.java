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

@SuppressWarnings("unused")
public class PlayerBean {
    private String playerId;
    private boolean bleeding;
    private boolean infected;
    private boolean bandaged;
    private boolean ointment;
    private boolean antibiotic;
    private Integer zombieKills = 0;
    private Integer playerKills = 0;
    private Integer playerHeals = 0;
    private Boolean playing;

    public PlayerBean(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public boolean isBleeding() {
        return bleeding;
    }

    public void setBleeding(Boolean bleeding) {
        this.bleeding = bleeding;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(Boolean infected) {
        this.infected = infected;
    }

    public boolean isBandaged() {
        return bandaged;
    }

    public void setBandaged(Boolean bandaged) {
        this.bandaged = bandaged;
    }

    public boolean isOintment() {
        return ointment;
    }

    public void setOintment(Boolean ointment) {
        this.ointment = ointment;
    }

    public boolean isAntibiotic() {
        return antibiotic;
    }

    public void setAntibiotic(Boolean antibiotic) {
        this.antibiotic = antibiotic;
    }

    public Integer getZombieKills() {
        return zombieKills;
    }

    public void setZombieKills(Integer zombieKills) {
        this.zombieKills = zombieKills;
    }

    public void addZombieKill() {
        zombieKills++;
    }

    public Integer getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(Integer playerKills) {
        this.playerKills = playerKills;
    }

    public void addPlayerKill() {
        playerKills++;
    }

    public Integer getPlayerHeals() {
        return playerHeals;
    }

    public void setPlayerHeals(Integer playerHeals) {
        this.playerHeals = playerHeals;
    }

    public void addPlayerHeal() {
        playerHeals++;
    }

    public Boolean isPlaying() {
        if (playing == null) {return false;}
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }
}
