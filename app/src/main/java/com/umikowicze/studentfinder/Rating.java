package com.umikowicze.studentfinder;

public class Rating {
    public String helperID;
    public String requesterID;
    public long rating;

    Rating(String helperID, String requesterID, long rating) {
        this.helperID = helperID;
        this.requesterID = requesterID;
        this.rating = rating;
    }
}
