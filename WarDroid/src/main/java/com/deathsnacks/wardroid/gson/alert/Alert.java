
package com.deathsnacks.wardroid.gson.alert;


import com.google.gson.annotations.Expose;


public class Alert {

    @Expose
    private com.deathsnacks.wardroid.gson.alert.Activation Activation;
    @Expose
    private int AllowReplay;
    @Expose
    private com.deathsnacks.wardroid.gson.alert.Expiry Expiry;
    @Expose
    private boolean ForceUnlock;
    @Expose
    private com.deathsnacks.wardroid.gson.alert.MissionInfo MissionInfo;
    @Expose
    private int Twitter;
    @Expose
    private _id _id;
    private boolean pc;

    public com.deathsnacks.wardroid.gson.alert.Activation getActivation() {
        return Activation;
    }

    public void setActivation(com.deathsnacks.wardroid.gson.alert.Activation Activation) {
        this.Activation = Activation;
    }

    public int getAllowReplay() {
        return AllowReplay;
    }

    public void setAllowReplay(int AllowReplay) {
        this.AllowReplay = AllowReplay;
    }

    public com.deathsnacks.wardroid.gson.alert.Expiry getExpiry() {
        return Expiry;
    }

    public void setExpiry(com.deathsnacks.wardroid.gson.alert.Expiry Expiry) {
        this.Expiry = Expiry;
    }

    public boolean isForceUnlock() {
        return ForceUnlock;
    }

    public void setForceUnlock(boolean ForceUnlock) {
        this.ForceUnlock = ForceUnlock;
    }

    public com.deathsnacks.wardroid.gson.alert.MissionInfo getMissionInfo() {
        return MissionInfo;
    }

    public void setMissionInfo(com.deathsnacks.wardroid.gson.alert.MissionInfo MissionInfo) {
        this.MissionInfo = MissionInfo;
    }

    public int getTwitter() {
        return Twitter;
    }

    public void setTwitter(int Twitter) {
        this.Twitter = Twitter;
    }

    public _id get_id() {
        return _id;
    }

    public void set_id(_id _id) {
        this._id = _id;
    }


    public boolean isPc() {
        return pc;
    }

    public void setPc(boolean pc) {
        this.pc = pc;
    }
}
