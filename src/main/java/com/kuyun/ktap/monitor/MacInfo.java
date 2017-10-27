package com.kuyun.ktap.monitor;

import java.io.Serializable;

public class MacInfo implements Serializable{


    private Long timestamp;

    private String mac;

    private String ip;

    public MacInfo(){

    }
    public MacInfo(Long timestamp, String mac) {
        this.timestamp = timestamp;
        this.mac = mac;
    }

    public MacInfo(String mac, String ip) {
        this.mac = mac;
        this.ip = ip;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return ip+"-"+mac;
    }
}
