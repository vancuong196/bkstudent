package com.kuon.bkstudent.models;

public class Notification {

    String time;
    String title;
    String content;


    public Notification(String time, String title, String content) {

        this.time = time.substring(0,19);
        this.title = title;
        this.content = content.replaceAll("<br>","\n");

    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
