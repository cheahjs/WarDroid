
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class PendingRecipe {

    @Expose
    private String ItemType;
    @Expose
    private com.deathsnacks.wardroid.gson.CompletionDate CompletionDate;

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String ItemType) {
        this.ItemType = ItemType;
    }

    public com.deathsnacks.wardroid.gson.CompletionDate getCompletionDate() {
        return CompletionDate;
    }

    public void setCompletionDate(com.deathsnacks.wardroid.gson.CompletionDate CompletionDate) {
        this.CompletionDate = CompletionDate;
    }

}
