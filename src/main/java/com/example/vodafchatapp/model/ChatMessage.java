package com.example.vodafchatapp.model;

public class ChatMessage {
    private String content;
    private String sender;
    private String password;
    private Boolean auth;
    private MessageType messageType;

    public enum MessageType {
        CHAT, LEAVE, JOIN
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
