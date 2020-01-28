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

    private int playerCounter = 0;
    private LogicShell shell;
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/game.sendMessage")
    @SendTo("/topic/public")
    public GameMessage sendMessage(@Payload GameMessage gameMessage) {
        GameMessage msg = shell.processMove(gameMessage.getPlayer(), gameMessage.getContent());
        //tu jeszcze wywolanie removeStone
        return msg;
    }

    @MessageMapping("/game.addPlayer")
    @SendTo("/topic/color")
    public GameMessage addUser() {
        GameMessage msg = new GameMessage();
        if (playerCounter == 0){
            shell = new LogicShell();
            msg.setPlayer("black");
        } else {
            msg.setPlayer("white");
        }
        playerCounter++;
        return msg;
    }

    public void removeStone (String id){
        template.convertAndSend("/topic/public", id);
    }

}
