package com.deathsnacks.wardroid.utils;

import android.app.Application;

/**
 * Created by Admin on 24/01/14.
 */
public class GlobalApplication extends Application {
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce+"";
    }

    private String displayName;
    private String accountId;
    private String nonce;

}
