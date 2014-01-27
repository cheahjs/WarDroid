
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


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
