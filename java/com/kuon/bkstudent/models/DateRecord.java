package com.kuon.bkstudent.models;

public class DateRecord {
    private String date;
    private String time;

    public DateRecord(String date, String time) {
        this.date = date;
        if (!time.equals("NA")){
            this.time = time.substring(0,8);
        } else {
            this.time = time;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
