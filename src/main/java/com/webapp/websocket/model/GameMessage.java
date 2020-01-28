package com.webapp.websocket.model;

public class GameMessage {
    private String content;
    private String player;
    private MessageType type;

    public enum MessageType {
        MOVE,
        JOIN,
        ERROR,
        REMOVE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
