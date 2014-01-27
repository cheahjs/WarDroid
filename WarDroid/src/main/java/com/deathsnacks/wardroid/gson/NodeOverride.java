
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class NodeOverride {

    @Expose
    private _id _id;
    @Expose
    private String Node;
    @Expose
    private String Faction;
    @Expose
    private String LevelOverride;
    @Expose
    private String EnemySpec;
    @Expose
    private String VipAgent;

    public _id get_id() {
        return _id;
    }

    public void set_id(_id _id) {
        this._id = _id;
    }

    public String getNode() {
        return Node;
    }

    public void setNode(String Node) {
        this.Node = Node;
    }

    public String getFaction() {
        return Faction;
    }

    public void setFaction(String Faction) {
        this.Faction = Faction;
    }

    public String getLevelOverride() {
        return LevelOverride;
    }

    public void setLevelOverride(String LevelOverride) {
        this.LevelOverride = LevelOverride;
    }

    public String getEnemySpec() {
        return EnemySpec;
    }

    public void setEnemySpec(String EnemySpec) {
        this.EnemySpec = EnemySpec;
    }

    public String getVipAgent() {
        return VipAgent;
    }

    public void setVipAgent(String VipAgent) {
        this.VipAgent = VipAgent;
    }

}
