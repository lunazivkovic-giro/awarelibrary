package com.aware.plugin.awarelibrary.models;

public class Algorithm {
    private Double reward1;
    private Double reward2;
    private Double reward3;
    private Double reward4;
    private boolean unanswered1;
    private boolean unanswered2;
    private boolean unanswered3;
    private boolean unanswered4;
    private String deviceId;

    public Algorithm() {
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getReward1() {
        return reward1;
    }

    public void setReward1(Double reward1) {
        this.reward1 = reward1;
    }

    public Double getReward2() {
        return reward2;
    }

    public void setReward2(Double reward2) {
        this.reward2 = reward2;
    }

    public Double getReward3() {
        return reward3;
    }

    public void setReward3(Double reward3) {
        this.reward3 = reward3;
    }

    public Double getReward4() {
        return reward4;
    }

    public void setReward4(Double reward4) {
        this.reward4 = reward4;
    }

    public boolean isUnanswered1() {
        return unanswered1;
    }

    public void setUnanswered1(boolean unanswered1) {
        this.unanswered1 = unanswered1;
    }

    public boolean isUnanswered2() {
        return unanswered2;
    }

    public void setUnanswered2(boolean unanswered2) {
        this.unanswered2 = unanswered2;
    }

    public boolean isUnanswered3() {
        return unanswered3;
    }

    public void setUnanswered3(boolean unanswered3) {
        this.unanswered3 = unanswered3;
    }

    public boolean isUnanswered4() {
        return unanswered4;
    }

    public void setUnanswered4(boolean unanswered4) {
        this.unanswered4 = unanswered4;
    }

    @Override
    public String toString() {
        return "Algorithm{" +
                ", reward1=" + reward1 +
                ", reward2=" + reward2 +
                ", reward3=" + reward3 +
                ", reward4=" + reward4 +
                ", unanswered1=" + unanswered1 +
                ", unanswered2=" + unanswered2 +
                ", unanswered3=" + unanswered3 +
                ", unanswered4=" + unanswered4 +
                '}';
    }
}
