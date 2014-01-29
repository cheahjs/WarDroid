
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class Member {

    @Expose
    private String ActiveAvatarImageType;
    @Expose
    private String DisplayName;
    @Expose
    private com.deathsnacks.wardroid.gson.LastLogin LastLogin;
    @Expose
    private int Rank;
    @Expose
    private int Status;
    @Expose
    private com.deathsnacks.wardroid.gson._id _id;

    public String getActiveAvatarImageType() {
        return ActiveAvatarImageType;
    }

    public void setActiveAvatarImageType(String ActiveAvatarImageType) {
        this.ActiveAvatarImageType = ActiveAvatarImageType;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    public com.deathsnacks.wardroid.gson.LastLogin getLastLogin() {
        return LastLogin;
    }

    public void setLastLogin(com.deathsnacks.wardroid.gson.LastLogin LastLogin) {
        this.LastLogin = LastLogin;
    }

    public int getRank() {
        return Rank;
    }

    public void setRank(int Rank) {
        this.Rank = Rank;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public com.deathsnacks.wardroid.gson._id get_id() {
        return _id;
    }

    public void set_id(com.deathsnacks.wardroid.gson._id _id) {
        this._id = _id;
    }

}
