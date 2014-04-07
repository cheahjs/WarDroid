
package com.deathsnacks.wardroid.gson.badlands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class MissionReward {

    @Expose
    private Integer credits;
    @Expose
    private Integer xp;
    @Expose
    private List<String> items = new ArrayList<String>();
    @Expose
    private List<CountedItem> countedItems = new ArrayList<CountedItem>();
    @Expose
    private String randomizedItems;
    @Expose
    private Boolean hidden;

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<CountedItem> getCountedItems() {
        return countedItems;
    }

    public void setCountedItems(List<CountedItem> countedItems) {
        this.countedItems = countedItems;
    }

    public String getRandomizedItems() {
        return randomizedItems;
    }

    public void setRandomizedItems(String randomizedItems) {
        this.randomizedItems = randomizedItems;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

}
