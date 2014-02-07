package com.deathsnacks.wardroid.utils.httpclasses;

/**
 * Created by Admin on 07/02/14.
 */
public class Invasion {
    private String id;
    private String node;
    private String region;
    private String invadingFaction;
    private String invadingType;
    private String invadingReward;
    private String invadingLevel;
    private String invadingAiSpec;
    private String defendingFaction;
    private String defendingType;
    private String defendingReward;
    private String defendingLevel;
    private String defendingAiSpec;

    public String getId() {
        return id;
    }

    public String getNode() {
        return node;
    }

    public String getRegion() {
        return region;
    }

    public String getInvadingFaction() {
        return invadingFaction;
    }

    public String getInvadingType() {
        return invadingType;
    }

    public String getInvadingReward() {
        return invadingReward;
    }

    public String getInvadingLevel() {
        return invadingLevel;
    }

    public String getInvadingAiSpec() {
        return invadingAiSpec;
    }

    public String getDefendingFaction() {
        return defendingFaction;
    }

    public String getDefendingType() {
        return defendingType;
    }

    public String getDefendingReward() {
        return defendingReward;
    }

    public String getDefendingLevel() {
        return defendingLevel;
    }

    public String getDefendingAiSpec() {
        return defendingAiSpec;
    }

    public long getActivation() {
        return activation;
    }

    public String getCount() {
        return count;
    }

    public String getGoal() {
        return goal;
    }

    public String getPercent() {
        return percent;
    }

    public String getEta() {
        return eta;
    }

    public String getDescription() {
        return description;
    }

    private long activation;
    private String count;
    private String goal;
    private String percent;
    private String eta;
    private String description;

    public Invasion(String raw) {
        String[] data = raw.split("\\|");
        id = data[0];
        node = data[1];
        region = data[2];
        invadingFaction = data[3];
        invadingType = data[4];
        invadingReward = data[5];
        invadingLevel = data[6];
        invadingAiSpec = data[7];
        defendingFaction = data[8];
        defendingType = data[9];
        defendingReward = data[10];
        defendingLevel = data[11];
        defendingAiSpec = data[12];
        activation = Long.parseLong(data[13]);
        count = data[14];
        goal = data[15];
        percent = data[16];
        eta = data[17];
        description = data[18];
    }
}
