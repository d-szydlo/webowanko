package com.webapp.websocket.controller;

import com.webapp.websocket.model.GameMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public GameMessage sendMessage(@Payload GameMessage gameMessage) {
        return gameMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public GameMessage addUser(@Payload GameMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
