
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class DefenderReward {

    @Expose
    private int credits;

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

}
