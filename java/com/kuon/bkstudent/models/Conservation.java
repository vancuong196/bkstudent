package com.kuon.bkstudent.models;

public class Conservation {
    private String datetime;
    private String creatorName;
    private String numberOfchat;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getNumberOfchat() {
        return numberOfchat;
    }

    public void setNumberOfchat(String numberOfchat) {
        this.numberOfchat = numberOfchat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Conservation(String datetime, String creatorName, String numberOfchat, String title, String id) {

        this.datetime = datetime.substring(0,19);
        this.creatorName = creatorName;
        this.numberOfchat = numberOfchat;
        this.title = title;
        this.id = id;
    }
}
