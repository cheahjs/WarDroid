
package com.deathsnacks.wardroid.gson;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;


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
