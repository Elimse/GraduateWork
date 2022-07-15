package com.example.diplomchik20;

import java.util.Date;

public class message {
    public String userName;
    public String textMessage;
    private long messageTime;


    public message(){}
    public message(String userName, String textMessage){
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = new Date().getTime();
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getTextMessage(){
        return textMessage;
    }

    public void setTextMessage(String textMessage){
        this.textMessage = textMessage;
    }

    public long getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(long messageTime){
        this.messageTime = messageTime;
    }
}
