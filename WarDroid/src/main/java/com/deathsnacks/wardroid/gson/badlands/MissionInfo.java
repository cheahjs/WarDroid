
package com.deathsnacks.wardroid.gson.badlands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class MissionInfo {

    @Expose
    private String missionType;
    @Expose
    private String faction;
    @Expose
    private String uniqueName;
    @Expose
    private String icon;
    @Expose
    private Boolean unlocked;
    @Expose
    private Integer completions;
    @Expose
    private Integer seed;
    @Expose
    private Integer difficulty;
    @Expose
    private MissionReward missionReward;
    @Expose
    private MissionRewardExtra missionRewardExtra;
    @Expose
    private String descText;
    @Expose
    private String introText;
    @Expose
    private String completionText;
    @Expose
    private String location;
    @Expose
    private String levelOverride;
    @Expose
    private String enemySpec;
    @Expose
    private List<Object> enemies = new ArrayList<Object>();
    @Expose
    private String vipAgent;
    @Expose
    private Integer minEnemyLevel;
    @Expose
    private Integer maxEnemyLevel;
    @Expose
    private Integer maxWaveNum;
    @Expose
    private String completeTag;
    @Expose
    private String alertId;
    @Expose
    private String alertTag;
    @Expose
    private String goalId;
    @Expose
    private String invasionId;
    @Expose
    private String invasionAllyFaction;
    @Expose
    private String levelKeyName;
    @Expose
    private Boolean nightmare;
    @Expose
    private String badlandSupport;
    @Expose
    private Integer badlandCreditsTaxRate;
    @Expose
    private Integer badlandMemberCreditsTaxRate;
    @Expose
    private Integer badlandItemsTaxRate;
    @Expose
    private Integer badlandMemberItemsTaxRate;

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

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Integer getCompletions() {
        return completions;
    }

    public void setCompletions(Integer completions) {
        this.completions = completions;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public MissionReward getMissionReward() {
        return missionReward;
    }

    public void setMissionReward(MissionReward missionReward) {
        this.missionReward = missionReward;
    }

    public MissionRewardExtra getMissionRewardExtra() {
        return missionRewardExtra;
    }

    public void setMissionRewardExtra(MissionRewardExtra missionRewardExtra) {
        this.missionRewardExtra = missionRewardExtra;
    }

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getCompletionText() {
        return completionText;
    }

    public void setCompletionText(String completionText) {
        this.completionText = completionText;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public List<Object> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Object> enemies) {
        this.enemies = enemies;
    }

    public String getVipAgent() {
        return vipAgent;
    }

    public void setVipAgent(String vipAgent) {
        this.vipAgent = vipAgent;
    }

    public Integer getMinEnemyLevel() {
        return minEnemyLevel;
    }

    public void setMinEnemyLevel(Integer minEnemyLevel) {
        this.minEnemyLevel = minEnemyLevel;
    }

    public Integer getMaxEnemyLevel() {
        return maxEnemyLevel;
    }

    public void setMaxEnemyLevel(Integer maxEnemyLevel) {
        this.maxEnemyLevel = maxEnemyLevel;
    }

    public Integer getMaxWaveNum() {
        return maxWaveNum;
    }

    public void setMaxWaveNum(Integer maxWaveNum) {
        this.maxWaveNum = maxWaveNum;
    }

    public String getCompleteTag() {
        return completeTag;
    }

    public void setCompleteTag(String completeTag) {
        this.completeTag = completeTag;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getAlertTag() {
        return alertTag;
    }

    public void setAlertTag(String alertTag) {
        this.alertTag = alertTag;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getInvasionId() {
        return invasionId;
    }

    public void setInvasionId(String invasionId) {
        this.invasionId = invasionId;
    }

    public String getInvasionAllyFaction() {
        return invasionAllyFaction;
    }

    public void setInvasionAllyFaction(String invasionAllyFaction) {
        this.invasionAllyFaction = invasionAllyFaction;
    }

    public String getLevelKeyName() {
        return levelKeyName;
    }

    public void setLevelKeyName(String levelKeyName) {
        this.levelKeyName = levelKeyName;
    }

    public Boolean getNightmare() {
        return nightmare;
    }

    public void setNightmare(Boolean nightmare) {
        this.nightmare = nightmare;
    }

    public String getBadlandSupport() {
        return badlandSupport;
    }

    public void setBadlandSupport(String badlandSupport) {
        this.badlandSupport = badlandSupport;
    }

    public Integer getBadlandCreditsTaxRate() {
        return badlandCreditsTaxRate;
    }

    public void setBadlandCreditsTaxRate(Integer badlandCreditsTaxRate) {
        this.badlandCreditsTaxRate = badlandCreditsTaxRate;
    }

    public Integer getBadlandMemberCreditsTaxRate() {
        return badlandMemberCreditsTaxRate;
    }

    public void setBadlandMemberCreditsTaxRate(Integer badlandMemberCreditsTaxRate) {
        this.badlandMemberCreditsTaxRate = badlandMemberCreditsTaxRate;
    }

    public Integer getBadlandItemsTaxRate() {
        return badlandItemsTaxRate;
    }

    public void setBadlandItemsTaxRate(Integer badlandItemsTaxRate) {
        this.badlandItemsTaxRate = badlandItemsTaxRate;
    }

    public Integer getBadlandMemberItemsTaxRate() {
        return badlandMemberItemsTaxRate;
    }

    public void setBadlandMemberItemsTaxRate(Integer badlandMemberItemsTaxRate) {
        this.badlandMemberItemsTaxRate = badlandMemberItemsTaxRate;
    }

}
