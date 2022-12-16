package com.thesis.myapplication.activities.chatbot;

public class ChatsModal {

    private String messsage;
    private String sender;

    public ChatsModal(String messsage, String sender) {
        this.messsage = messsage;
        this.sender = sender;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
