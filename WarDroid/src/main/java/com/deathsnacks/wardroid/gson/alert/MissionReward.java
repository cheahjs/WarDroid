
package com.deathsnacks.wardroid.gson.alert;

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
            rtn += NumberFormat.getIntegerInstance().format(credits) + "*c";
        if (countedItems.size() > 0) {
            for (int i = 0; i < countedItems.size(); i++) {
                String rawtxt;
                CountedItem item = countedItems.get(i);
                if (item.getItemCount() == 1) {
                    rawtxt = item.getItemType();
                } else {
                    rawtxt = item.getItemCount() + " " + item.getItemType();
                }
                rtn += " - " + rawtxt;
            }
        }
        if (items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                String rawtxt;
                String item = items.get(i);
                rawtxt = item;
                rtn += " - " + rawtxt;
            }
        }
        return rtn;
    }
}
