
package com.deathsnacks.wardroid.gson;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class Guild {

    @Expose
    private com.deathsnacks.wardroid.gson.AllianceId AllianceId;
    @Expose
    private String MOTD;
    @Expose
    private List<Member> Members = new ArrayList<Member>();
    @Expose
    private String Name;
    @Expose
    private List<Rank> Ranks = new ArrayList<Rank>();
    @Expose
    private int Tier;
    @Expose
    private int TradeTax;
    @Expose
    private com.deathsnacks.wardroid.gson.Vault Vault;
    @Expose
    private _id _id;

    public com.deathsnacks.wardroid.gson.AllianceId getAllianceId() {
        return AllianceId;
    }

    public void setAllianceId(com.deathsnacks.wardroid.gson.AllianceId AllianceId) {
        this.AllianceId = AllianceId;
    }

    public String getMOTD() {
        return MOTD;
    }

    public void setMOTD(String MOTD) {
        this.MOTD = MOTD;
    }

    public List<Member> getMembers() {
        return Members;
    }

    public void setMembers(List<Member> Members) {
        this.Members = Members;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public List<Rank> getRanks() {
        return Ranks;
    }

    public void setRanks(List<Rank> Ranks) {
        this.Ranks = Ranks;
    }

    public int getTier() {
        return Tier;
    }

    public void setTier(int Tier) {
        this.Tier = Tier;
    }

    public int getTradeTax() {
        return TradeTax;
    }

    public void setTradeTax(int TradeTax) {
        this.TradeTax = TradeTax;
    }

    public com.deathsnacks.wardroid.gson.Vault getVault() {
        return Vault;
    }

    public void setVault(com.deathsnacks.wardroid.gson.Vault Vault) {
        this.Vault = Vault;
    }

    public _id get_id() {
        return _id;
    }

    public void set_id(_id _id) {
        this._id = _id;
    }

}
