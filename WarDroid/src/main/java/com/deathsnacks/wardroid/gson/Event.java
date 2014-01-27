
package com.deathsnacks.wardroid.gson;


import com.google.gson.annotations.Expose;


public class Event {

    @Expose
    private com.deathsnacks.wardroid.gson._id _id;
    @Expose
    private String Msg;
    @Expose
    private String Prop;
    @Expose
    private com.deathsnacks.wardroid.gson.Date Date;

    public com.deathsnacks.wardroid.gson._id get_id() {
        return _id;
    }

    public void set_id(com.deathsnacks.wardroid.gson._id _id) {
        this._id = _id;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getProp() {
        return Prop;
    }

    public void setProp(String Prop) {
        this.Prop = Prop;
    }

    public com.deathsnacks.wardroid.gson.Date getDate() {
        return Date;
    }

    public void setDate(com.deathsnacks.wardroid.gson.Date Date) {
        this.Date = Date;
    }

}
