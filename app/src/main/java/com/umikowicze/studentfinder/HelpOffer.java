package com.umikowicze.studentfinder;

public class HelpOffer {

    private String area;
    private String userid;

    public HelpOffer() {

    }

    public HelpOffer(String area, String userid) {
        this.area = area;
        this.userid = userid;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
