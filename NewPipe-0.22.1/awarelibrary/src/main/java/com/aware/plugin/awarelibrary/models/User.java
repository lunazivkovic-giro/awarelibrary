package com.aware.plugin.awarelibrary.models;

public class User {

       private String id;
       private String address;
        private String workAddress;
       private int extroversion;
        private int agreeableness;
        private int conscientiousness;
        private int neuroticism;
        private int openness;
        private String deviceId;
        private int numDay;
        private int numTotal;


    public User() {
    }

    public User(String id, String address, String workAddress, int extroversion, int agreeableness, int conscientiousness, int neuroticism, int openness, String deviceId, int numDay, int numTotal) {
        this.id = id;
        this.address = address;
        this.workAddress = workAddress;
        this.extroversion = extroversion;
        this.agreeableness = agreeableness;
        this.conscientiousness = conscientiousness;
        this.neuroticism = neuroticism;
        this.openness = openness;
        this.deviceId = deviceId;
        this.numDay = numDay;
        this.numTotal = numTotal;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getNumDay() {
        return numDay;
    }

    public void setNumDay(int numDay) {
        this.numDay = numDay;
    }

    public int getNumTotal() {
        return numTotal;
    }

    public void setNumTotal(int numTotal) {
        this.numTotal = numTotal;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getExtroversion() {
        return extroversion;
    }

    public void setExtroversion(int extroversion) {
        this.extroversion = extroversion;
    }

    public int getAgreeableness() {
        return agreeableness;
    }

    public void setAgreeableness(int agreeableness) {
        this.agreeableness = agreeableness;
    }

    public int getConscientiousness() {
        return conscientiousness;
    }

    public void setConscientiousness(int conscientiousness) {
        this.conscientiousness = conscientiousness;
    }

    public int getNeuroticism() {
        return neuroticism;
    }

    public void setNeuroticism(int neuroticism) {
        this.neuroticism = neuroticism;
    }

    public int getOpenness() {
        return openness;
    }

    public void setOpenness(int openness) {
        this.openness = openness;
    }
}
