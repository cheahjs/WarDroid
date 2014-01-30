package com.deathsnacks.wardroid.utils;

import android.app.Activity;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Deathmax on 10/20/13.
 */
public class Http {
    private static OkHttpClient client = new OkHttpClient();

    //endpoint = endpoint.php, args = &foo=bar
    public static String getApi(Activity act, String endpoint, String args) throws IOException {
        if (act == null)
            throw new IOException("Activity is null.");
        GlobalApplication app = ((GlobalApplication) act.getApplication());
        URL url = new URL("https://api.warframe.com/api/" + endpoint + "?accountId=" +
                app.getAccountId() + "&nonce=" + app.getNonce() + "&platform=PC" + args);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "");
        InputStream in = null;
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            String responseStr = new String(response, "UTF-8");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("deathsnacks", "error with api: " + responseStr);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST || endpoint.equals("checkPendingRecipes.php")) {
                    if (responseStr.length() < 2)
                        responseStr = "Authentication failure";
                }
            }
            if (responseStr.contains("Authentication failure")) {
                app.setAccountId(null);
                app.setDisplayName(null);
                app.setNonce(0);
            }
            return responseStr;
        } finally {
            if (in != null) in.close();
        }
    }

    //endpoint = endpoint.php, args = &foo=bar
    public static String postApi(Activity act, String endpoint, String args, byte[] data) throws IOException {
        if (act == null)
            throw new IOException("Activity is null.");
        GlobalApplication app = ((GlobalApplication) act.getApplication());
        URL url = new URL("https://api.warframe.com/api/" + endpoint + "?accountId=" +
                app.getAccountId() + "&nonce=" + app.getNonce() + "&platform=PC" + args);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "");
        InputStream in = null;
        OutputStream out = null;
        try {
            connection.setRequestMethod("POST");
            out = connection.getOutputStream();
            out.write(data);
            out.close();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            String responseStr = new String(response, "UTF-8");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("deathsnacks", "error with post api: " + responseStr);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    if (responseStr.length() < 2)
                        responseStr = "Authentication failure";
                }
            }
            if (responseStr.contains("Authentication failure")) {
                app.setAccountId(null);
                app.setDisplayName(null);
                app.setNonce(0);
            }
            return responseStr;
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    //endpoint = endpoint.php, args = &foo=bar
    public static String getStats(Activity act, String endpoint, String args) throws IOException {
        if (act == null)
            throw new IOException("Activity is null.");
        GlobalApplication app = ((GlobalApplication) act.getApplication());
        URL url = new URL("https://stats.warframe.com/stats/" + endpoint + "?accountId=" +
                app.getAccountId() + "&nonce=" + app.getNonce() + "&platform=PC" + args);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "");
        InputStream in = null;
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            String responseStr = new String(response, "UTF-8");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("deathsnacks", "error with stats api: " + responseStr);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    if (responseStr.length() < 2)
                        responseStr = "Authentication failure";
                }
            }
            if (responseStr.contains("Authentication failure")) {
                app.setAccountId(null);
                app.setDisplayName(null);
                app.setNonce(0);
            }
            return responseStr;
        } finally {
            if (in != null) in.close();
        }
    }

    public static String get(Activity act, String Url) throws IOException {
        GlobalApplication app = ((GlobalApplication) act.getApplication());
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

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }
}
