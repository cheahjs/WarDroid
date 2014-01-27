package com.deathsnacks.wardroid.utils;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.squareup.okhttp.*;

/**
 * Created by Deathmax on 10/20/13.
 */
public class Http {
    private static OkHttpClient client = new OkHttpClient();

    //endpoint = endpoint.php, args = &foo=bar
    public static String getApi(Activity act, String endpoint, String args) throws IOException {
        GlobalApplication app = ((GlobalApplication)act.getApplication());
        URL url = new URL("https://api.warframe.com/api/" + endpoint + "?accountId=" +
                app.getAccountId() + "&nonce=" + app.getNonce() + "&platform=PC" + args);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "");
        InputStream in = null;
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            }
            else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("deathsnacks", "error with api: " + new String(response, "UTF-8"));
            }
            return new String(response, "UTF-8");
        }
        finally {
            if (in != null) in.close();
        }
    }

    //endpoint = endpoint.php, args = &foo=bar
    public static String getStats(Activity act, String endpoint, String args) throws IOException {
        GlobalApplication app = ((GlobalApplication)act.getApplication());
        URL url = new URL("https://stats.warframe.com/stats/" + endpoint + "?accountId=" +
                app.getAccountId() + "&nonce=" + app.getNonce() + "&platform=PC" + args);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "");
        InputStream in = null;
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            }
            else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            return new String(response, "UTF-8");
        }
        finally {
            if (in != null) in.close();
        }
    }

    public static String get(Activity act, String Url) throws IOException {
        GlobalApplication app = ((GlobalApplication)act.getApplication());
        URL url = new URL(Url);
        HttpURLConnection connection = client.open(url);
        System.setProperty("http.agent", "");
        connection.setRequestProperty("User-Agent", "WarDroid/Android");
        InputStream in = null;
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            }
            else {
                in = connection.getInputStream();
            }
            byte[] response = readAll(in);
            return new String(response, "UTF-8");
        }
        finally {
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
    /*public static String requestGetApi(String endpoint, String args)
    {
        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet("https://api.warframe.com/api/" + endpoint +
        "?accountId=" + LoginJson.Id + "&nonce=" + LoginJson.Nonce + "&platform=PC" + args);
        AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
        String result = "";
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream inputStream = AndroidHttpClient.getUngzippedContent(entity);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8);
            String line = "";
            while ((line = reader.readLine()) != null)
                result += line + "\n";
            return result;
        }
        catch (Exception e) {

        }
        return "";
    }

    public static String requestLogin(String email, String password)
    {
        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet("https://api.warframe.com/api/login.php");
        AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
        String result = "";
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream inputStream = AndroidHttpClient.getUngzippedContent(entity);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 8);
            String line = "";
            while ((line = reader.readLine()) != null)
                result += line + "\n";
            return result;
        }
        catch (Exception e) {

        }
        return "";
    }*/
}
