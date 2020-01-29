package com.webapp.websocket.controller;

import com.webapp.websocket.exceptions.CoordinatesOutOfBoundsException;
import com.webapp.websocket.exceptions.IntersectionTakenException;
import com.webapp.websocket.exceptions.KoRuleViolatedException;
import com.webapp.websocket.exceptions.SuicidalMoveException;
import com.webapp.websocket.logic.AppEngine;
import com.webapp.websocket.model.GameMessage;

import java.util.ArrayList;

import static com.webapp.websocket.model.GameMessage.MessageType.*;

public class LogicShell {

    private AppEngine engine;
    private String currentPlayer;

    public LogicShell(){
        engine = new AppEngine(19);
        currentPlayer = "black";
    }

    public GameMessage processMove(String player, String coords){
        GameMessage msg = new GameMessage();
        System.out.println(player);
        msg.setPlayer(player);
        msg.setType(MOVE);
        if (!player.equals(currentPlayer)){
            msg.setContent("Nie twoj ruch");
            msg.setType(ERROR);
        } else {
            int joint = Integer.parseInt(coords);
            int X = joint%19;
            joint -= X;
            int Y = joint/19;
            try {
                engine.handleMove(X,Y);
            } catch (KoRuleViolatedException e) {
                msg.setType(ERROR);
                msg.setContent("Naruszona zasada KO");
            } catch (CoordinatesOutOfBoundsException e) {
                msg.setType(ERROR);
                msg.setContent("W sumie nie bedzie tego bledu");
            } catch (SuicidalMoveException e) {
                msg.setType(ERROR);
                msg.setContent("Ruch samobojczy");
            } catch (IntersectionTakenException e) {
                msg.setType(ERROR);
                msg.setContent("Pole zajete");
            }
            if (msg.getType().equals(MOVE)){
                msg.setContent(coords);
                changePlayer();
            }
        }
        return msg;
    }


    public ArrayList<GameMessage> getRemoveMsg(){
        ArrayList<GameMessage> rem = new ArrayList<>();
        GameMessage msg = new GameMessage();
        msg.setType(REMOVE);
        String chng = engine.getChanges();
        int i=0;
        StringBuilder builder = new StringBuilder();
        int var = 0;
        while (i<chng.length()){
            while (chng.charAt(i) != ' '){
                builder.append(chng.charAt(i));
                i++;
            }
            String helper = builder.toString();
            var = Integer.parseInt(helper);
            builder.setLength(0);
            i++;
            while (chng.charAt(i) != ' '){
                builder.append(chng.charAt(i));
                i++;
            }
            helper = builder.toString();
            var += Integer.parseInt(helper)*19;
            i++;
            msg.setContent(Integer.toString(var));
            rem.add(msg);
        }
        engine.resetChanges();
        System.out.println(rem.size());
        return rem;
    }

    private void changePlayer(){
        if(currentPlayer.equals("black"))
            currentPlayer = "white";
        else
            currentPlayer = "black";

    }

}
