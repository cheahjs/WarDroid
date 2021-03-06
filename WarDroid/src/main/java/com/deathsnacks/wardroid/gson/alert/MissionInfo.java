
package com.deathsnacks.wardroid.gson.alert;


import com.google.gson.annotations.Expose;


public class MissionInfo {

    @Expose
    private String descText;
    @Expose
    private String location;
    @Expose
    private String missionType;
    @Expose
    private String faction;
    @Expose
    private int seed;
    @Expose
    private double difficulty;
    @Expose
    private MissionReward missionReward;
    @Expose
    private String levelOverride;
    @Expose
    private String enemySpec;
    @Expose
    private String vipAgent;
    @Expose
    private int minEnemyLevel;
    @Expose
    private int maxEnemyLevel;
    @Expose
    private int maxWaveNum;
    @Expose
    private boolean nightmare;

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
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

    public String getVipAgent() {
        return vipAgent;
    }

    public void setVipAgent(String vipAgent) {
        this.vipAgent = vipAgent;
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

    public int getMaxWaveNum() {
        return maxWaveNum;
    }

    public void setMaxWaveNum(int maxWaveNum) {
        this.maxWaveNum = maxWaveNum;
    }

    public boolean isNightmare() {
        return nightmare;
    }

    public void setNightmare(boolean nightmare) {
        this.nightmare = nightmare;
    }

}
