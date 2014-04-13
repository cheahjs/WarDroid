
package com.deathsnacks.wardroid.gson.badlands;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AttackerInfo {

    @SerializedName("CreditsTaxRate")
    @Expose
    private Integer creditsTaxRate;
    @SerializedName("MemberCreditsTaxRate")
    @Expose
    private Integer memberCreditsTaxRate;
    @SerializedName("ItemsTaxRate")
    @Expose
    private Integer itemsTaxRate;
    @SerializedName("MemberItemsTaxRate")
    @Expose
    private Integer memberItemsTaxRate;
    @Expose
    private com.deathsnacks.wardroid.gson.badlands.TaxChangeAllowedTime TaxChangeAllowedTime;
    @SerializedName("IsAlliance")
    @Expose
    private Boolean isAlliance;
    @SerializedName("Id")
    @Expose
    private Id id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("StrengthRemaining")
    @Expose
    private Integer strengthRemaining;
    @SerializedName("MaxStrength")
    @Expose
    private Integer maxStrength;
    @SerializedName("DeploymentActivationTime")
    @Expose
    private DeploymentActivationTime deploymentActivationTime;
    @SerializedName("RailType")
    @Expose
    private String railType;
    @SerializedName("MOTD")
    @Expose
    private String mOTD;
    @SerializedName("LastHealTime")
    @Expose
    private LastHealTime lastHealTime;
    @SerializedName("HealRate")
    @Expose
    private Double healRate;
    @SerializedName("DamagePerMission")
    @Expose
    private Integer damagePerMission;
    @SerializedName("MissionInfo")
    @Expose
    private MissionInfo missionInfo;

    public Integer getCreditsTaxRate() {
        return creditsTaxRate;
    }

    public void setCreditsTaxRate(Integer creditsTaxRate) {
        this.creditsTaxRate = creditsTaxRate;
    }

    public Integer getMemberCreditsTaxRate() {
        return memberCreditsTaxRate;
    }

    public void setMemberCreditsTaxRate(Integer memberCreditsTaxRate) {
        this.memberCreditsTaxRate = memberCreditsTaxRate;
    }

    public Integer getItemsTaxRate() {
        return itemsTaxRate;
    }

    public void setItemsTaxRate(Integer itemsTaxRate) {
        this.itemsTaxRate = itemsTaxRate;
    }

    public Integer getMemberItemsTaxRate() {
        return memberItemsTaxRate;
    }

    public void setMemberItemsTaxRate(Integer memberItemsTaxRate) {
        this.memberItemsTaxRate = memberItemsTaxRate;
    }

    public Boolean getIsAlliance() {
        return isAlliance;
    }

    public void setIsAlliance(Boolean isAlliance) {
        this.isAlliance = isAlliance;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStrengthRemaining() {
        return strengthRemaining;
    }

    public void setStrengthRemaining(Integer strengthRemaining) {
        this.strengthRemaining = strengthRemaining;
    }

    public Integer getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(Integer maxStrength) {
        this.maxStrength = maxStrength;
    }

    public DeploymentActivationTime getDeploymentActivationTime() {
        return deploymentActivationTime;
    }

    public void setDeploymentActivationTime(DeploymentActivationTime deploymentActivationTime) {
        this.deploymentActivationTime = deploymentActivationTime;
    }

    public String getRailType() {
        return railType;
    }

    public void setRailType(String railType) {
        this.railType = railType;
    }

    public String getMOTD() {
        return mOTD;
    }

    public void setMOTD(String mOTD) {
        this.mOTD = mOTD;
    }

    public LastHealTime getLastHealTime() {
        return lastHealTime;
    }

    public void setLastHealTime(LastHealTime lastHealTime) {
        this.lastHealTime = lastHealTime;
    }

    public Double getHealRate() {
        return healRate;
    }

    public void setHealRate(Double healRate) {
        this.healRate = healRate;
    }

    public Integer getDamagePerMission() {
        return damagePerMission;
    }

    public void setDamagePerMission(Integer damagePerMission) {
        this.damagePerMission = damagePerMission;
    }

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public void setMissionInfo(MissionInfo missionInfo) {
        this.missionInfo = missionInfo;
    }

    public TaxChangeAllowedTime getTaxChangeAllowedTime() {
        return TaxChangeAllowedTime;
    }

    public void setTaxChangeAllowedTime(TaxChangeAllowedTime taxChangeAllowedTime) {
        TaxChangeAllowedTime = taxChangeAllowedTime;
    }
}
