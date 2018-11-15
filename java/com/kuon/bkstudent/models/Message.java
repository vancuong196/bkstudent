package com.kuon.bkstudent.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class Message {
    private String userName;
    private String userID;
    private String content;
    private String time;
    

    public Message(String userID, String userName,String content, String time) {
        this.userID = userID;
        this.userName = userName;
        this.time = time;
        this.content = content;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getUserId() {
        return this.userID;
    }

    public void setUserId(String userId) {
        this.userID = userId;
    }
    public String getUsername() {
        return this.userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }
    public ChatMessage toChatMessage(ChatMessage.Type type){
            return new ChatMessage("From : "+this.userName+"\n"+this.content,getTime(this.time),type);
    }
    private long getTime(String datetime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date a = format.parse(datetime);
            return a.getTime();
        } catch (ParseException e) {
            return 1212;
        }

    }
}
