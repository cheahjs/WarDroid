
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class PendingRecipes {

    @Expose
    private List<PendingRecipe> PendingRecipes = new ArrayList<PendingRecipe>();

    public List<PendingRecipe> getPendingRecipes() {
        return PendingRecipes;
    }

    public void setPendingRecipes(List<PendingRecipe> PendingRecipes) {
        this.PendingRecipes = PendingRecipes;
    }

}
