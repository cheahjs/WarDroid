package com.deathsnacks.wardroid.gson.alert;

/**
 * Created by Admin on 27/01/14.
 */
public class CountedItem {
    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public int getItemCount() {
        return ItemCount;
    }

    public void setItemCount(int itemCount) {
        ItemCount = itemCount;
    }

    private String ItemType;
    private int ItemCount;
}
