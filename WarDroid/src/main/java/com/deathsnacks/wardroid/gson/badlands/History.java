
package com.deathsnacks.wardroid.gson.badlands;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class History {

    @SerializedName("Def")
    @Expose
    private String def;
    @SerializedName("DefId")
    @Expose
    private DefId defId;
    @SerializedName("DefAli")
    @Expose
    private Boolean defAli;
    @SerializedName("Att")
    @Expose
    private String att;
    @SerializedName("AttId")
    @Expose
    private AttId attId;
    @SerializedName("AttAli")
    @Expose
    private Boolean attAli;
    @SerializedName("WinId")
    @Expose
    private WinId winId;
    @SerializedName("Start")
    @Expose
    private Start start;
    @SerializedName("End")
    @Expose
    private End end;

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public DefId getDefId() {
        return defId;
    }

    public void setDefId(DefId defId) {
        this.defId = defId;
    }

    public Boolean getDefAli() {
        return defAli;
    }

    public void setDefAli(Boolean defAli) {
        this.defAli = defAli;
    }

    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    public AttId getAttId() {
        return attId;
    }

    public void setAttId(AttId attId) {
        this.attId = attId;
    }

    public Boolean getAttAli() {
        return attAli;
    }

    public void setAttAli(Boolean attAli) {
        this.attAli = attAli;
    }

    public WinId getWinId() {
        return winId;
    }

    public void setWinId(WinId winId) {
        this.winId = winId;
    }

    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public End getEnd() {
        return end;
    }

    public void setEnd(End end) {
        this.end = end;
    }

}
