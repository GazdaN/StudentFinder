package com.umikowicze.studentfinder;

public class Helper {

    private String name;
    private String photoUrl;
    private long ratings;
    private long stars;
    private boolean location;

    public Helper() {
    }

    public Helper(String name, String photoUrl, long stars, long ratings, String location) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.ratings = ratings;
        this.stars = stars;
        this.location = Boolean.parseBoolean(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean getLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public double getRating() {
        System.out.println(stars + " " + ratings*5);
        double rating = (double) stars / (ratings*5);
        return rating;
    }

    public Long getRatings() {
        return ratings;
    }

    public void setRatings(Long ratings) {
        this.ratings = ratings;
    }

    public Long getStars() {
        return stars;
    }

    public void setStars(Long stars) {
        this.stars = stars;
    }

}
