
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class Rank {

    @Expose
    private String Name;
    @Expose
    private int Permissions;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getPermissions() {
        return Permissions;
    }

    public void setPermissions(int Permissions) {
        this.Permissions = Permissions;
    }

}
