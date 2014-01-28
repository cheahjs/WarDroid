
package com.deathsnacks.wardroid.gson;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;


public class ActiveDrones {

    @Expose
    private List<ActiveDrone> ActiveDrones = new ArrayList<ActiveDrone>();

    public List<ActiveDrone> getActiveDrones() {
        return ActiveDrones;
    }

    public void setActiveDrones(List<ActiveDrone> ActiveDrones) {
        this.ActiveDrones = ActiveDrones;
    }

}
