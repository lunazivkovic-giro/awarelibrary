package com.aware.plugin.awarelibrary.models;

public class State {
    private int activity;
    private Double longitude;
    private Double latitude;
    private Integer light;
    private Integer battery;
    private Integer value;
    private String homeOrWork;
    private String stars;
    private String deviceId;


    public State() {
    }

    public State(int activity, Double longitude, Double latitude, Integer light, Integer battery, Integer value, String homeOrWork, String stars, String deviceId) {
        this.activity = activity;
        this.longitude = longitude;
        this.latitude = latitude;
        this.light = light;
        this.battery = battery;
        this.value = value;
        this.homeOrWork = homeOrWork;
        this.stars = stars;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getValue() {
        return value;
    }

    public String getHomeOrWork() {
        return homeOrWork;
    }

    public void setHomeOrWork(String homeOrWork) {
        this.homeOrWork = homeOrWork;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public int getActivity() {
        return activity;
    }

    public Integer getLight() {
        return light;
    }

    public void setLight(Integer light) {
        this.light = light;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }
}
