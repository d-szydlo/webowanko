package com.webapp.websocket.controller;

import com.webapp.websocket.model.GameMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    int playerCounter = 0;
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/game.sendMessage")
    @SendTo("/topic/public")
    public GameMessage sendMessage(@Payload GameMessage gameMessage) {
        return gameMessage;
    }

    @MessageMapping("/game.addPlayer")
    @SendTo("/topic/color")
    public GameMessage addUser() {
        GameMessage msg = new GameMessage();
        if (playerCounter == 0){
            msg.setSender("black");
        } else {
            msg.setSender("white");
        }
        playerCounter++;
        return msg;
    }

    public void removeStone (String id){
        template.convertAndSend("/topic/remove", id);
    }

}
