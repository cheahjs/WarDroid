package com.deathsnacks.wardroid.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Deathmax on 10/20/13.
 */
public class Http {
    private static final String TAG = "Http";
    private static OkHttpClient client = new OkHttpClient();

    public static String get(String Url) throws IOException {
        URL url = new URL(Url);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "WarDroid/Android/");
        InputStream in = null;
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            return new String(response, "UTF-8");
        } finally {
            if (in != null) in.close();
        }
    }

    public static String get(String Url, long lastModified, String cache, SharedPreferences.Editor editor, String key) throws IOException {
        URL url = new URL(Url);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "WarDroid/Android/");
        if (lastModified != 0) {
            connection.setIfModifiedSince(lastModified);
        }
        InputStream in = null;
        try {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String txt = new String(response, "UTF-8");
                editor.putLong(key + "_modified", connection.getLastModified());
                editor.putString(key + "_cache", txt);
                editor.commit();
                return txt;
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                Log.d(TAG, "Cache hit on " + key + " cache: " + cache);
                return cache;
            } else {
                throw new IOException("Http get returned " + connection.getResponseCode());
            }
        } finally {
            if (in != null) in.close();
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }
}
