
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class AttackerReward {

    @Expose
    private int credits;

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

}
