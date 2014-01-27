
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class ActiveDrone {

    @Expose
    private com.deathsnacks.wardroid.gson.DeployTime DeployTime;
    @Expose
    private int System;
    @Expose
    private String ItemType;
    @Expose
    private List<Resource> Resources = new ArrayList<Resource>();
    @Expose
    private com.deathsnacks.wardroid.gson.DamageTime DamageTime;
    @Expose
    private int PendingDamage;
    @Expose
    private int CurrentHP;
    @Expose
    private com.deathsnacks.wardroid.gson.ItemId ItemId;

    public com.deathsnacks.wardroid.gson.DeployTime getDeployTime() {
        return DeployTime;
    }

    public void setDeployTime(com.deathsnacks.wardroid.gson.DeployTime DeployTime) {
        this.DeployTime = DeployTime;
    }

    public int getSystem() {
        return System;
    }

    public void setSystem(int System) {
        this.System = System;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String ItemType) {
        this.ItemType = ItemType;
    }

    public List<Resource> getResources() {
        return Resources;
    }

    public void setResources(List<Resource> Resources) {
        this.Resources = Resources;
    }

    public com.deathsnacks.wardroid.gson.DamageTime getDamageTime() {
        return DamageTime;
    }

    public void setDamageTime(com.deathsnacks.wardroid.gson.DamageTime DamageTime) {
        this.DamageTime = DamageTime;
    }

    public int getPendingDamage() {
        return PendingDamage;
    }

    public void setPendingDamage(int PendingDamage) {
        this.PendingDamage = PendingDamage;
    }

    public int getCurrentHP() {
        return CurrentHP;
    }

    public void setCurrentHP(int CurrentHP) {
        this.CurrentHP = CurrentHP;
    }

    public com.deathsnacks.wardroid.gson.ItemId getItemId() {
        return ItemId;
    }

    public void setItemId(com.deathsnacks.wardroid.gson.ItemId ItemId) {
        this.ItemId = ItemId;
    }

}
