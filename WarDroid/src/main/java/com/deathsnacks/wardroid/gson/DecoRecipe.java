
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class DecoRecipe {

    @Expose
    private int ItemCount;
    @Expose
    private String ItemType;

    public int getItemCount() {
        return ItemCount;
    }

    public void setItemCount(int ItemCount) {
        this.ItemCount = ItemCount;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String ItemType) {
        this.ItemType = ItemType;
    }

}
