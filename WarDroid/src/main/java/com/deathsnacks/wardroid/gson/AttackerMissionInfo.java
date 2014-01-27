
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class AttackerMissionInfo {

    @Expose
    private String missionType;
    @Expose
    private String faction;
    @Expose
    private int seed;
    @Expose
    private int difficulty;
    @Expose
    private MissionReward missionReward;
    @Expose
    private String levelOverride;
    @Expose
    private String enemySpec;
    @Expose
    private int minEnemyLevel;
    @Expose
    private int maxEnemyLevel;

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public MissionReward getMissionReward() {
        return missionReward;
    }

    public void setMissionReward(MissionReward missionReward) {
        this.missionReward = missionReward;
    }

    public String getLevelOverride() {
        return levelOverride;
    }

    public void setLevelOverride(String levelOverride) {
        this.levelOverride = levelOverride;
    }

    public String getEnemySpec() {
        return enemySpec;
    }

    public void setEnemySpec(String enemySpec) {
        this.enemySpec = enemySpec;
    }

    public int getMinEnemyLevel() {
        return minEnemyLevel;
    }

    public void setMinEnemyLevel(int minEnemyLevel) {
        this.minEnemyLevel = minEnemyLevel;
    }

    public int getMaxEnemyLevel() {
        return maxEnemyLevel;
    }

    public void setMaxEnemyLevel(int maxEnemyLevel) {
        this.maxEnemyLevel = maxEnemyLevel;
    }

}
