
package com.deathsnacks.wardroid.gson;

import com.deathsnacks.wardroid.utils.Names;
import com.google.gson.annotations.Expose;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class MissionReward {

    @Expose
    private int credits;
    @Expose
    private int xp;
    @Expose
    private List<String> items = new ArrayList<String>();
    @Expose
    private List<CountedItem> countedItems = new ArrayList<CountedItem>();

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

    public String getRewardString() {
        String rtn = "";
        if (credits > 0)
            rtn += NumberFormat.getIntegerInstance().format(credits) + "cr";
        if (countedItems.size() > 0) {
            String rawtxt = "";
            CountedItem item = countedItems.get(0);
            if (item.getItemCount() == 1) {
                rawtxt = Names.getName(null, item.getItemType());
            } else {
                rawtxt = item.getItemCount() + " " + Names.getName(null, item.getItemType());
            }
            rtn += " - " + rawtxt;
        }
        if (items.size() > 0) {
            String rawtxt = "";
            String item = items.get(0);
            rawtxt = Names.getName(null, item);
            rtn += " - " + rawtxt;
        }
        return rtn;
    }
}
