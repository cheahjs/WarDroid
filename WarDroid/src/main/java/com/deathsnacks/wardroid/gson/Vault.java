
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class Vault {

    @Expose
    private List<DecoRecipe> DecoRecipes = new ArrayList<DecoRecipe>();
    @Expose
    private List<DojoRefundMiscItem> DojoRefundMiscItems = new ArrayList<DojoRefundMiscItem>();
    @Expose
    private int DojoRefundPremiumCredits;
    @Expose
    private int DojoRefundRegularCredits;

    public List<DecoRecipe> getDecoRecipes() {
        return DecoRecipes;
    }

    public void setDecoRecipes(List<DecoRecipe> DecoRecipes) {
        this.DecoRecipes = DecoRecipes;
    }

    public List<DojoRefundMiscItem> getDojoRefundMiscItems() {
        return DojoRefundMiscItems;
    }

    public void setDojoRefundMiscItems(List<DojoRefundMiscItem> DojoRefundMiscItems) {
        this.DojoRefundMiscItems = DojoRefundMiscItems;
    }

    public int getDojoRefundPremiumCredits() {
        return DojoRefundPremiumCredits;
    }

    public void setDojoRefundPremiumCredits(int DojoRefundPremiumCredits) {
        this.DojoRefundPremiumCredits = DojoRefundPremiumCredits;
    }

    public int getDojoRefundRegularCredits() {
        return DojoRefundRegularCredits;
    }

    public void setDojoRefundRegularCredits(int DojoRefundRegularCredits) {
        this.DojoRefundRegularCredits = DojoRefundRegularCredits;
    }

}
