
package com.deathsnacks.wardroid.gson.badlands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BadlandNode {

    @Expose
    private com.deathsnacks.wardroid.gson.badlands._id _id;
    @SerializedName("Node")
    @Expose
    private String node;
    @SerializedName("NodeDisplayName")
    @Expose
    private String nodeDisplayName;
    @SerializedName("DefenderInfo")
    @Expose
    private DefenderInfo defenderInfo;
    @SerializedName("AttackerInfo")
    @Expose
    private AttackerInfo attackerInfo;
    @SerializedName("ConflictExpiration")
    @Expose
    private ConflictExpiration conflictExpiration;
    @SerializedName("History")
    @Expose
    private List<History> history = new ArrayList<History>();
    @SerializedName("NodeRegionName")
    @Expose
    private String nodeRegionName;
    private String NodeGameType;

    public com.deathsnacks.wardroid.gson.badlands._id get_id() {
        return _id;
    }

    public void set_id(com.deathsnacks.wardroid.gson.badlands._id _id) {
        this._id = _id;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getNodeDisplayName() {
        return nodeDisplayName;
    }

    public void setNodeDisplayName(String nodeDisplayName) {
        this.nodeDisplayName = nodeDisplayName;
    }

    public DefenderInfo getDefenderInfo() {
        return defenderInfo;
    }

    public void setDefenderInfo(DefenderInfo defenderInfo) {
        this.defenderInfo = defenderInfo;
    }

    public AttackerInfo getAttackerInfo() {
        return attackerInfo;
    }

    public void setAttackerInfo(AttackerInfo attackerInfo) {
        this.attackerInfo = attackerInfo;
    }

    public ConflictExpiration getConflictExpiration() {
        return conflictExpiration;
    }

    public void setConflictExpiration(ConflictExpiration conflictExpiration) {
        this.conflictExpiration = conflictExpiration;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }

    public String getNodeRegionName() {
        return nodeRegionName;
    }

    public void setNodeRegionName(String nodeRegionName) {
        this.nodeRegionName = nodeRegionName;
    }

    public String getNodeGameType() {
        return NodeGameType;
    }

    public void setNodeGameType(String nodeGameType) {
        NodeGameType = nodeGameType;
    }
}
