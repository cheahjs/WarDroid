
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class Invasion {

    @Expose
    private Activation Activation;
    @Expose
    private com.deathsnacks.wardroid.gson.AttackerMissionInfo AttackerMissionInfo;
    @Expose
    private com.deathsnacks.wardroid.gson.AttackerReward AttackerReward;
    @Expose
    private boolean Completed;
    @Expose
    private int Count;
    @Expose
    private com.deathsnacks.wardroid.gson.DefenderMissionInfo DefenderMissionInfo;
    @Expose
    private com.deathsnacks.wardroid.gson.DefenderReward DefenderReward;
    @Expose
    private String Faction;
    @Expose
    private int Goal;
    @Expose
    private String LocTag;
    @Expose
    private String Node;
    @Expose
    private _id _id;

    public Activation getActivation() {
        return Activation;
    }

    public void setActivation(Activation Activation) {
        this.Activation = Activation;
    }

    public com.deathsnacks.wardroid.gson.AttackerMissionInfo getAttackerMissionInfo() {
        return AttackerMissionInfo;
    }

    public void setAttackerMissionInfo(com.deathsnacks.wardroid.gson.AttackerMissionInfo AttackerMissionInfo) {
        this.AttackerMissionInfo = AttackerMissionInfo;
    }

    public com.deathsnacks.wardroid.gson.AttackerReward getAttackerReward() {
        return AttackerReward;
    }

    public void setAttackerReward(com.deathsnacks.wardroid.gson.AttackerReward AttackerReward) {
        this.AttackerReward = AttackerReward;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean Completed) {
        this.Completed = Completed;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }

    public com.deathsnacks.wardroid.gson.DefenderMissionInfo getDefenderMissionInfo() {
        return DefenderMissionInfo;
    }

    public void setDefenderMissionInfo(com.deathsnacks.wardroid.gson.DefenderMissionInfo DefenderMissionInfo) {
        this.DefenderMissionInfo = DefenderMissionInfo;
    }

    public com.deathsnacks.wardroid.gson.DefenderReward getDefenderReward() {
        return DefenderReward;
    }

    public void setDefenderReward(com.deathsnacks.wardroid.gson.DefenderReward DefenderReward) {
        this.DefenderReward = DefenderReward;
    }

    public String getFaction() {
        return Faction;
    }

    public void setFaction(String Faction) {
        this.Faction = Faction;
    }

    public int getGoal() {
        return Goal;
    }

    public void setGoal(int Goal) {
        this.Goal = Goal;
    }

    public String getLocTag() {
        return LocTag;
    }

    public void setLocTag(String LocTag) {
        this.LocTag = LocTag;
    }

    public String getNode() {
        return Node;
    }

    public void setNode(String Node) {
        this.Node = Node;
    }

    public _id get_id() {
        return _id;
    }

    public void set_id(_id _id) {
        this._id = _id;
    }

}
