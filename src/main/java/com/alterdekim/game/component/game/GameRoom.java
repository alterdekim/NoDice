package com.alterdekim.game.component.game;

import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.websocket.message.BasicMessage;
import com.alterdekim.game.websocket.message.ResponseMessage;
import com.alterdekim.game.websocket.message.WebSocketMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class GameRoom {

    @Getter
    private List<GamePlayer> players;

    private ConcurrentHashMap<Long, WebSocketSession> socks;

    private UserServiceImpl userService;

    private List<BoardField> boardFields;

    public GameRoom(List<RoomPlayer> players, UserServiceImpl userService) {
        this.userService = userService;
        this.players = players.stream()
                .map(p -> new GamePlayer(p.getUserId(), userService.findById(p.getUserId()).getDisplayName(), 0, new Chip(p.getUserId(), 0, 0, "#000000")))
                .collect(Collectors.toList());
        this.socks = new ConcurrentHashMap<>();
        this.boardFields = new ArrayList<>();
    }

    public void receiveMessage(BasicMessage message, WebSocketSession session) {
        if(players.stream().noneMatch(p -> p.getUserId().longValue() == message.getUid().longValue())) return;
        socks.put(message.getUid(), session);
        log.info("receiveMessage " + message.getType());
        parseMessage(message);
    }

    private void parseMessage(BasicMessage message) {
        switch (message.getType()) {
            case InfoRequest:
                sendAllInfoRequest(message);
                break;
        }
    }

    private void sendAllInfoRequest(BasicMessage message) {
        try {
            ObjectMapper om = new ObjectMapper();
            List<BoardTile> top = new ArrayList<>();
            List<BoardTile> right = new ArrayList<>();
            List<BoardTile> bottom = new ArrayList<>();
            List<BoardTile> left = new ArrayList<>();

            for( int i = 0; i < 9; i++ ) {
                top.add(new BoardTile(UUID.randomUUID().toString(), 2 + i, 1000, "", "/static/images/7up.png", "ffffff", "f5f5f5"));
            }

            for( int i = 0; i < 9; i++ ) {
                right.add(new BoardTile(UUID.randomUUID().toString(), i, 1400, "", "/static/images/fanta.png", "bbbbbb", "f5f5f5"));
            }

            for( int i = 0; i < 9; i++ ) {
                bottom.add(new BoardTile(UUID.randomUUID().toString(), 2 + i, 1800, "", "/static/images/cola.png", "eeeeee", "f5f5f5"));
            }

            for( int i = 10; i >= 2; i-- ) {
                left.add(new BoardTile(UUID.randomUUID().toString(), i, 2200, "", "/static/images/beeline.png", "000000", "f5f5f5"));
            }



            List<CornerTile> corners = new ArrayList<>();
            corners.add(new CornerTile("/static/images/start.png"));
            corners.add(new CornerTile("/static/images/injail.png"));
            corners.add(new CornerTile("/static/images/parking.png"));
            corners.add(new CornerTile("/static/images/gotojail.png"));

            BoardGUI boardGUI = new BoardGUI(top, right, bottom, left, corners);
            sendMessage(message.getUid(), WebSocketMessageType.PlayersList, om.writeValueAsString(players));
            sendMessage(message.getUid(), WebSocketMessageType.BoardGUI, om.writeValueAsString(boardGUI));
            left.get(2).setCost(12345);
            left.get(2).setImg("/static/images/fanta.png");
            left.get(2).setColor("bcbcbc");
            left.get(2).setStars("★★★");
            left.get(2).setOwnerColor("fffbbb");
            sendMessage(message.getUid(), WebSocketMessageType.ChangeBoardTileState, om.writeValueAsString(left.get(2)));

            Chip red = new Chip(2L, 10, 9, "#ff0000");
            Chip green = new Chip(3L, 10, 10, "#00ff00");
            Chip blue = new Chip(4L, 0, 5, "#0000ff");
            Chip white = new Chip(5L, 0, 5, "#ffffff");
            Chip black = new Chip(6L, 0, 5, "#000000");

            sendMessage(message.getUid(), WebSocketMessageType.AssignChip, om.writeValueAsString(red));
            sendMessage(message.getUid(), WebSocketMessageType.AssignChip, om.writeValueAsString(green));
            sendMessage(message.getUid(), WebSocketMessageType.AssignChip, om.writeValueAsString(blue));
            sendMessage(message.getUid(), WebSocketMessageType.AssignChip, om.writeValueAsString(white));
            sendMessage(message.getUid(), WebSocketMessageType.AssignChip, om.writeValueAsString(black));

            red.setY(10);
            sendMessage(message.getUid(), WebSocketMessageType.ChipMove, om.writeValueAsString(red));

            sendMessage(message.getUid(), WebSocketMessageType.PlayerColor, om.writeValueAsString(new PlayerColor(2L, "#ff0000")));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendMessage(Long userId, WebSocketMessageType type, String message) {
        try {
            if (socks.get(userId).isOpen())
                socks.get(userId).sendMessage(
                        new TextMessage(
                                new ObjectMapper().writeValueAsString(
                                        new ResponseMessage(type, message)
                                )
                        )
                );
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
