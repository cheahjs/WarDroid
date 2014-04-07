
package com.deathsnacks.wardroid.gson.badlands;


import com.google.gson.annotations.Expose;


public class DeploymentActivationTime {

    @Expose
    private Integer sec;
    @Expose
    private Integer usec;

    public Integer getSec() {
        return sec;
    }

    public void setSec(Integer sec) {
        this.sec = sec;
    }

    public Integer getUsec() {
        return usec;
    }

    public void setUsec(Integer usec) {
        this.usec = usec;
    }

}
