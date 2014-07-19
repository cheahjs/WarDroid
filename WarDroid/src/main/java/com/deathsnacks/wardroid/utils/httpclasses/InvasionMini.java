package com.deathsnacks.wardroid.utils.httpclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 04/02/14.
 */
public class InvasionMini {
    private String id;
    private String node;
    private String region;
    private String invadingFaction;
    private String invadingType;
    private String invadingReward;
    private String defendingFaction;
    private String defendingType;
    private String defendingReward;
    private long activation;
    private String description;

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

    public String getDefendingFaction() {
        return defendingFaction;
    }

    public String getDefendingType() {
        return defendingType;
    }

    public String getDefendingReward() {
        return defendingReward;
    }

    public long getActivation() {
        return activation;
    }

    public String getDescription() {
        return description;
    }

    public String[] getRewards() {
        List<String> rewards = new ArrayList<String>();
        if (!invadingFaction.equals("Infestation"))
            rewards.add(invadingReward.replaceAll("\\d+ ", "").replace(" Blueprint", ""));
        rewards.add(defendingReward.replaceAll("\\d+ ", "").replace(" Blueprint", ""));
        return rewards.toArray(new String[rewards.size()]);
    }

    public String getNotificationText(String platform) {
        String base = String.format("Invasion (%s) : ", platform);
        if (!invadingFaction.equals("Infestation"))
            base += String.format("<b>%s</b> & ", invadingReward);
        base += String.format("<b>%s</b>", defendingReward);
        return base;
    }

    public InvasionMini(String raw) {
        String[] data = raw.split("\\|");
        id = data[0];
        node = data[1];
        region = data[2];
        invadingFaction = data[3];
        invadingType = data[4];
        invadingReward = data[5];
        defendingFaction = data[6];
        defendingType = data[7];
        defendingReward = data[8];
        activation = Long.parseLong(data[9]);
        description = data[10];
    }
}