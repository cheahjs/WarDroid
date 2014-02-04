package com.deathsnacks.wardroid.utils.httpclasses;

/**
 * Created by Admin on 04/02/14.
 */
public class Alert {
    private String id;
    private String node;
    private String region;
    private String mission;

    public String getId() {
        return id;
    }

    public String getNode() {
        return node;
    }

    public String getRegion() {
        return region;
    }

    public String getMission() {
        return mission;
    }

    public String getFaction() {
        return faction;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public long getActivation() {
        return activation;
    }

    public long getExpiry() {
        return expiry;
    }

    public String[] getRewards() {
        return rewards;
    }

    public String getDescription() {
        return description;
    }

    private String faction;
    private String min;
    private String max;
    private long activation;
    private long expiry;
    private String[] rewards;
    private String description;

    public Alert(String raw) {
        String[] data = raw.split("\\|");
        id = data[0];
        node = data[1];
        region = data[2];
        mission = data[3];
        faction = data[4];
        min = data[5];
        max = data[6];
        activation = Long.parseLong(data[7]);
        expiry = Long.parseLong(data[8]);
        rewards = data[9].split(" - ");
        description = data[10];
    }
}
