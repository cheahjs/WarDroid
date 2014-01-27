package com.deathsnacks.wardroid.gson;

/**
 * Created by Admin on 26/01/14.
 */
public class SaveLogin {
    public String getPasswordhash() {
        return passwordhash;
    }

    public SaveLogin(String email, String passwordhash) {
        this.email = email;
        this.passwordhash = passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
    private String passwordhash;


}
