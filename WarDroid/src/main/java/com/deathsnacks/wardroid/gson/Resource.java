
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class Resource {

    @Expose
    private String ItemType;
    @Expose
    private int BinTotal;
    @Expose
    private com.deathsnacks.wardroid.gson.StartTime StartTime;

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String ItemType) {
        this.ItemType = ItemType;
    }

    public int getBinTotal() {
        return BinTotal;
    }

    public void setBinTotal(int BinTotal) {
        this.BinTotal = BinTotal;
    }

    public com.deathsnacks.wardroid.gson.StartTime getStartTime() {
        return StartTime;
    }

    public void setStartTime(com.deathsnacks.wardroid.gson.StartTime StartTime) {
        this.StartTime = StartTime;
    }

}
