package com.kuon.bkstudent.models;

import java.util.ArrayList;

public class UserInfo {
    private String name;
    private String id;
    private int totalDate;
    private int counted;
    private double percent;
    private ArrayList<DateRecord> dates;

    public UserInfo(String name, String id, int totalDate, int counted, double percent, ArrayList<DateRecord> dates) {
        this.name = name;
        this.id = id;
        this.totalDate = totalDate;
        this.counted = counted;
        this.percent = percent;
        this.dates = dates;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", totalDate=" + totalDate +
                ", counted=" + counted +
                ", percent=" + percent +
                ", dates=" + dates +
                '}';
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalDate() {
        return totalDate;
    }

    public void setTotalDate(int totalDate) {
        this.totalDate = totalDate;
    }

    public int getCounted() {
        return counted;
    }

    public void setCounted(int counted) {
        this.counted = counted;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public ArrayList<DateRecord> getDates() {
        return dates;
    }

    public void setDates(ArrayList<DateRecord> dates) {
        this.dates = dates;
    }
}
