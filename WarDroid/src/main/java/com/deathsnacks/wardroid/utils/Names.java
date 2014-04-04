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
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Admin on 26/01/14.
 */
public class Names {
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
