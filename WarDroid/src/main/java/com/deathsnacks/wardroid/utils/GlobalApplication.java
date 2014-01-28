package com.deathsnacks.wardroid.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Admin on 24/01/14.
 */
public class GlobalApplication extends Application {
    public String getDisplayName() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("DisplayName", null);
    }

    public void setDisplayName(String displayName) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putString("DisplayName", displayName);
        editor.commit();
    }

    public String getAccountId() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("AccountId", null);
    }

    public void setAccountId(String accountId) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putString("AccountId", accountId);
        editor.commit();
    }

    public String getNonce() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("Nonce", null);
    }

    public void setNonce(int nonce) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putString("Nonce", nonce + "");
        editor.commit();
    }

    public String getEmail() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("Email", null);
    }

    public void setEmail(String email) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putString("Email", email);
        editor.commit();
    }

    public String getHashedPassword() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("HashedPassword", null);
    }

    public void setHashedPassword(String hashedPassword) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putString("HashedPassword", hashedPassword);
        editor.commit();
    }

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
}
