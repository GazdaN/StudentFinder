package com.umikowicze.studentfinder;

public class HelpRequest {

    private String area;
    private String helperid;
    private String requesterid;
    private String status;
    private String datetime;

    public HelpRequest() {

    }

    public HelpRequest(String area, String helperid, String requesterid, String status, String datetime) {
        this.area = area;
        this.helperid = helperid;
        this.requesterid = requesterid;
        this.status = status;
        this.datetime = datetime;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHelperid() {
        return helperid;
    }

    public void setHelperid(String helperid) {
        this.helperid = helperid;
    }

    public String getRequesterid() {
        return requesterid;
    }

    public void setRequesterid(String requesterid) {
        this.requesterid = requesterid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
