package com.deathsnacks.wardroid.utils;

import android.app.Activity;

import com.deathsnacks.wardroid.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Admin on 26/01/14.
 */
public class Names {
    private static HashMap<String, String> Strings = null;
    private static HashMap<String, String> Names = null;
    private static HashMap<String, String> PlanetNames = null;

    public static String getName(Activity act, String raw) {
        if (Names == null) {
            if (act == null || act.getResources() == null) {
                return raw;
            }
            Gson gson = (new GsonBuilder().create());
            InputStream rawResource = act.getResources().openRawResource(R.raw.names_12);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawResource));
            Type token = new TypeToken<HashMap<String, String>>() {
            }.getType();
            Names = gson.fromJson(reader, token);
            try {
                reader.close();
                rawResource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Names.containsKey(raw) ? Names.get(raw) : raw;
    }

    public static String getString(Activity act, String raw) {
        if (Strings == null) {
            if (act == null || act.getResources() == null) {
                return raw;
            }
            Gson gson = (new GsonBuilder().create());
            InputStream rawResource = act.getResources().openRawResource(R.raw.strings_12);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawResource));
            Type token = new TypeToken<HashMap<String, String>>() {
            }.getType();
            Strings = gson.fromJson(reader, token);
            try {
                reader.close();
                rawResource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Strings.containsKey(raw) ? Strings.get(raw) : raw;
    }

    public static String getNode(Activity act, String node) {
        if (PlanetNames == null) {
            if (act == null || act.getResources() == null) {
                return node;
            }
            Gson gson = (new GsonBuilder().create());
            InputStream rawResource = act.getResources().openRawResource(R.raw.planetnamesregion_12);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawResource));
            Type token = new TypeToken<HashMap<String, String>>() {
            }.getType();
            PlanetNames = gson.fromJson(reader, token);
            try {
                reader.close();
                rawResource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return PlanetNames.containsKey(node) ? PlanetNames.get(node).split("\\|")[1] : node;
    }

    public static String getRegion(Activity act, String node) {
        if (PlanetNames == null) {
            if (act == null || act.getResources() == null) {
                return "?";
            }
            Gson gson = (new GsonBuilder().create());
            InputStream rawResource = act.getResources().openRawResource(R.raw.planetnamesregion_12);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawResource));
            Type token = new TypeToken<HashMap<String, String>>() {
            }.getType();
            PlanetNames = gson.fromJson(reader, token);
            try {
                reader.close();
                rawResource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return PlanetNames.containsKey(node) ? PlanetNames.get(node).split("\\|")[0] : "?";
    }

    public static String getFaction(String raw) {
        raw = raw.toLowerCase();
        if (raw.contains("grin"))
            return "Grineer";
        else if (raw.contains("corp"))
            return "Corpus";
        else if (raw.contains("infes"))
            return "Infestation";
        else if (raw.contains("oroki"))
            return "Corrupted";
        return raw;
    }

    public static String getMissionType(String raw) {
        raw = raw.toLowerCase();
        if (raw.contains("mt_defense"))
            return "Defense";
        if (raw.contains("mt_assassination"))
            return "Assassination";
        if (raw.contains("mt_extermination"))
            return "Extermination";
        if (raw.contains("mt_survival"))
            return "Survival";
        if (raw.contains("mt_intel"))
            return "Spy";
        if (raw.contains("mt_capture"))
            return "Capture";
        if (raw.contains("mt_sabotage"))
            return "Sabotage";
        if (raw.contains("mt_counter_intel"))
            return "Deception";
        if (raw.contains("mt_rescue"))
            return "Rescue";
        if (raw.contains("mt_mobile_defense"))
            return "Mobile Defense";
        if (raw.contains("mt_territory"))
            return "Interception";
        return raw;
    }
}
