
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class MissionReward {

    @Expose
    private int credits;
    @Expose
    private int xp;
    @Expose
    private List<Object> items = new ArrayList<Object>();
    @Expose
    private List<Object> countedItems = new ArrayList<Object>();

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

    public List<Object> getCountedItems() {
        return countedItems;
    }

    public void setCountedItems(List<Object> countedItems) {
        this.countedItems = countedItems;
    }

}
