
package com.deathsnacks.wardroid.gson.badlands;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DefenderInfo {

    @SerializedName("BattlePayReserve")
    @Expose
    private Integer battlePayReserve;
    @SerializedName("CreditsTaxRate")
    @Expose
    private Integer creditsTaxRate;
    @SerializedName("DamagePerMission")
    @Expose
    private Integer damagePerMission;
    @SerializedName("DeploymentActivationTime")
    @Expose
    private DeploymentActivationTime deploymentActivationTime;
    @Expose
    private com.deathsnacks.wardroid.gson.badlands.TaxChangeAllowedTime TaxChangeAllowedTime;
    @SerializedName("HealRate")
    @Expose
    private Double healRate;
    @SerializedName("Id")
    @Expose
    private Id id;
    @SerializedName("IsAlliance")
    @Expose
    private Boolean isAlliance;
    @SerializedName("ItemsTaxRate")
    @Expose
    private Integer itemsTaxRate;
    @SerializedName("LastHealTime")
    @Expose
    private LastHealTime lastHealTime;
    @SerializedName("MOTD")
    @Expose
    private String mOTD;
    @SerializedName("MaxStrength")
    @Expose
    private Integer maxStrength;
    @SerializedName("MemberCreditsTaxRate")
    @Expose
    private Integer memberCreditsTaxRate;
    @SerializedName("MemberItemsTaxRate")
    @Expose
    private Integer memberItemsTaxRate;
    @SerializedName("MissionInfo")
    @Expose
    private MissionInfo missionInfo;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("RailType")
    @Expose
    private String railType;
    @SerializedName("StrengthRemaining")
    @Expose
    private Integer strengthRemaining;

    public Integer getBattlePayReserve() {
        return battlePayReserve;
    }

    public void setBattlePayReserve(Integer battlePayReserve) {
        this.battlePayReserve = battlePayReserve;
    }

    public Integer getCreditsTaxRate() {
        return creditsTaxRate;
    }

    public void setCreditsTaxRate(Integer creditsTaxRate) {
        this.creditsTaxRate = creditsTaxRate;
    }

    public Integer getDamagePerMission() {
        return damagePerMission;
    }

    public void setDamagePerMission(Integer damagePerMission) {
        this.damagePerMission = damagePerMission;
    }

    public DeploymentActivationTime getDeploymentActivationTime() {
        return deploymentActivationTime;
    }

    public void setDeploymentActivationTime(DeploymentActivationTime deploymentActivationTime) {
        this.deploymentActivationTime = deploymentActivationTime;
    }

    public Double getHealRate() {
        return healRate;
    }

    public void setHealRate(Double healRate) {
        this.healRate = healRate;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Boolean getIsAlliance() {
        return isAlliance;
    }

    public void setIsAlliance(Boolean isAlliance) {
        this.isAlliance = isAlliance;
    }

    public Integer getItemsTaxRate() {
        return itemsTaxRate;
    }

    public void setItemsTaxRate(Integer itemsTaxRate) {
        this.itemsTaxRate = itemsTaxRate;
    }

    public LastHealTime getLastHealTime() {
        return lastHealTime;
    }

    public void setLastHealTime(LastHealTime lastHealTime) {
        this.lastHealTime = lastHealTime;
    }

    public String getMOTD() {
        return mOTD;
    }

    public void setMOTD(String mOTD) {
        this.mOTD = mOTD;
    }

    public Integer getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(Integer maxStrength) {
        this.maxStrength = maxStrength;
    }

    public Integer getMemberCreditsTaxRate() {
        return memberCreditsTaxRate;
    }

    public void setMemberCreditsTaxRate(Integer memberCreditsTaxRate) {
        this.memberCreditsTaxRate = memberCreditsTaxRate;
    }

    public Integer getMemberItemsTaxRate() {
        return memberItemsTaxRate;
    }

    public void setMemberItemsTaxRate(Integer memberItemsTaxRate) {
        this.memberItemsTaxRate = memberItemsTaxRate;
    }

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public void setMissionInfo(MissionInfo missionInfo) {
        this.missionInfo = missionInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRailType() {
        return railType;
    }

    public void setRailType(String railType) {
        this.railType = railType;
    }

    public Integer getStrengthRemaining() {
        return strengthRemaining;
    }

    public void setStrengthRemaining(Integer strengthRemaining) {
        this.strengthRemaining = strengthRemaining;
    }

    public TaxChangeAllowedTime getTaxChangeAllowedTime() {
        return TaxChangeAllowedTime;
    }

    public void setTaxChangeAllowedTime(TaxChangeAllowedTime taxChangeAllowedTime) {
        TaxChangeAllowedTime = taxChangeAllowedTime;
    }
}
