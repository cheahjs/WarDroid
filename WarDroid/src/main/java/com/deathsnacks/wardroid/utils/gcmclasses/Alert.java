package com.deathsnacks.wardroid.utils.gcmclasses;

/**
 * Created by Admin on 24/02/14.
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

    public long getActivation() {
        return activation;
    }

    public long getExpiry() {
        return expiry;
    }

    public String[] getRewards() {
        return rewards;
    }

    private String faction;
    private long activation;
    private long expiry;
    private String[] rewards;

    public Alert(String raw) {
        String[] data = raw.split("\\|");
        id = data[0];
        node = data[1];
        region = data[2];
        mission = data[3];
        faction = data[4];
        activation = Long.parseLong(data[5]);
        expiry = Long.parseLong(data[6]);
        rewards = data[7].split(" - ");
    }
}
