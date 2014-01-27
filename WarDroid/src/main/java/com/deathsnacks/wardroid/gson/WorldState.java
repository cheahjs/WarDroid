
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class WorldState {

    @Expose
    private List<Event> Events = new ArrayList<Event>();
    @Expose
    private List<Object> Goals = new ArrayList<Object>();
    @Expose
    private List<Alert> Alerts = new ArrayList<Alert>();
    @Expose
    private List<Object> GlobalUpgrades = new ArrayList<Object>();
    @Expose
    private List<Object> FlashSales = new ArrayList<Object>();
    @Expose
    private List<Invasion> Invasions = new ArrayList<Invasion>();
    @Expose
    private List<NodeOverride> NodeOverrides = new ArrayList<NodeOverride>();
    @Expose
    private int Time;
    @Expose
    private String BuildLabel;

    public List<Event> getEvents() {
        return Events;
    }

    public void setEvents(List<Event> Events) {
        this.Events = Events;
    }

    public List<Object> getGoals() {
        return Goals;
    }

    public void setGoals(List<Object> Goals) {
        this.Goals = Goals;
    }

    public List<Alert> getAlerts() {
        return Alerts;
    }

    public void setAlerts(List<Alert> Alerts) {
        this.Alerts = Alerts;
    }

    public List<Object> getGlobalUpgrades() {
        return GlobalUpgrades;
    }

    public void setGlobalUpgrades(List<Object> GlobalUpgrades) {
        this.GlobalUpgrades = GlobalUpgrades;
    }

    public List<Object> getFlashSales() {
        return FlashSales;
    }

    public void setFlashSales(List<Object> FlashSales) {
        this.FlashSales = FlashSales;
    }

    public List<Invasion> getInvasions() {
        return Invasions;
    }

    public void setInvasions(List<Invasion> Invasions) {
        this.Invasions = Invasions;
    }

    public List<NodeOverride> getNodeOverrides() {
        return NodeOverrides;
    }

    public void setNodeOverrides(List<NodeOverride> NodeOverrides) {
        this.NodeOverrides = NodeOverrides;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int Time) {
        this.Time = Time;
    }

    public String getBuildLabel() {
        return BuildLabel;
    }

    public void setBuildLabel(String BuildLabel) {
        this.BuildLabel = BuildLabel;
    }

}
