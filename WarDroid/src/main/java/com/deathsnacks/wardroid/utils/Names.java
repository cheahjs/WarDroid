package com.deathsnacks.wardroid.utils;

import com.deathsnacks.wardroid.R;

import android.app.Activity;

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
    private static HashMap<String, String> Names  = null;

    public static String getName(Activity act, String raw) {
        if (Names == null) {
            Gson gson = (new GsonBuilder().create());
            InputStream rawResource = act.getResources().openRawResource(R.raw.names_11);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawResource));
            Type token = new TypeToken<HashMap<String, String>>(){}.getType();
            Names = gson.fromJson(reader, token);
            try {
                reader.close();
                rawResource.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Names.containsKey(raw) ? Names.get(raw) : raw;
    }

    public static String getString(Activity act, String raw) {
        if (Strings == null) {
            Gson gson = (new GsonBuilder().create());
            InputStream rawResource = act.getResources().openRawResource(R.raw.strings_11);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawResource));
            Type token = new TypeToken<HashMap<String, String>>(){}.getType();
            Strings = gson.fromJson(reader, token);
            try {
                reader.close();
                rawResource.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Strings.containsKey(raw) ? Strings.get(raw) : raw;
    }

    public static String getRegion(int id)
    {
        switch (id)
        {
            case 0:
                return "Mercury";
            case 1:
                return "Venus";
            case 2:
                return "Earth";
            case 3:
                return "Mars";
            case 4:
                return "Jupiter";
            case 5:
                return "Saturn";
            case 6:
                return "Uranus";
            case 7:
                return "Neptune";
            case 8:
                return "Pluto";
            case 9:
                return "Ceres";
            case 10:
                return "Eris";
            case 11:
                return "Sedna";
            case 12:
                return "Europa";
            default:
                return "-";
        }
    }
}
