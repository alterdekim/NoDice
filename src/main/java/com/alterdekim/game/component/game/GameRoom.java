package com.alterdekim.game.component.game;

import com.alterdekim.game.component.game.statemanager.StateManager;
import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.websocket.message.BasicMessage;
import com.alterdekim.game.websocket.message.ResponseMessage;
import com.alterdekim.game.websocket.message.WebSocketMessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class GameRoom extends Thread {

    @Getter
    private final List<GamePlayer> players;

    private final ConcurrentMap<Long, WebSocketSession> socks;

    private final UserServiceImpl userService;

    @Getter
    private BoardGUI board;

    private GameState state;

    private final ObjectMapper om;

    @Getter
    @Setter
    private boolean isGameLoopFrozen = false;

    private final ConcurrentMap<GameState, StateManager> manager;

    public GameRoom(List<RoomPlayer> players, UserServiceImpl userService) {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(ActionDialogBody.class)
                .allowIfBaseType(List.class)
                .build();
        this.om = new ObjectMapper();
        this.om.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        this.userService = userService;
        this.players = players.stream()
                .map(p -> new GamePlayer(p.getUserId(), userService.findById(p.getUserId()).getDisplayName(), 0, new Chip(p.getUserId(), 0, 0, "#000000")))
                .collect(Collectors.toList());
        GamePlayerColor c = GamePlayerColor.RED;
        for( int i = 0; i < this.players.size(); i++ ) {
            this.players.get(i).getChip().setColor(c.getHex());
            c = c.next();
        }
        this.socks = new ConcurrentHashMap<>();
        this.state = GameState.MOVE;
        this.manager = new ConcurrentHashMap<>();
        Arrays.stream(GameState.values()).forEach(s -> {
            try {
                this.manager.put(s, s.getManagerClass().getDeclaredConstructor().newInstance(this));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        this.initBoard();
        this.start();
    }

    private void initBoard() {
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

        this.board = new BoardGUI(top, right, bottom, left, corners);

        /*left.get(2).setCost(12345);
        left.get(2).setImg("/static/images/fanta.png");
        left.get(2).setColor("bcbcbc");
        left.get(2).setStars("★★★");
        left.get(2).setOwnerColor("fffbbb");
        sendMessage(message.getUid(), WebSocketMessageType.ChangeBoardTileState, left.get(2));*/

       /* Chip red = new Chip(2L, 10, 9, "#ff0000");
        Chip green = new Chip(3L, 10, 10, "#00ff00");
        Chip blue = new Chip(4L, 0, 5, "#0000ff");
        Chip white = new Chip(5L, 0, 5, "#ffffff");
        Chip black = new Chip(6L, 0, 5, "#000000");

        sendMessage(message.getUid(), WebSocketMessageType.AssignChip, red);
        sendMessage(message.getUid(), WebSocketMessageType.AssignChip, green);
        sendMessage(message.getUid(), WebSocketMessageType.AssignChip, blue);
        sendMessage(message.getUid(), WebSocketMessageType.AssignChip, white);
        sendMessage(message.getUid(), WebSocketMessageType.AssignChip, black);*/
        /*
        red.setY(10);
        sendMessage(message.getUid(), WebSocketMessageType.ChipMove, red);

        sendMessage(message.getUid(), WebSocketMessageType.PlayerColor, new PlayerColor(2L, "#ff0000"));

        List<DialogButton> buttons = new ArrayList<>();
        buttons.add(new DialogButton("Button1", DialogButtonColor.GREEN, Collections.singletonList(GameMsgType.DialogConfirmAnswer)));
        DialogButtonsList b = new DialogButtonsList(buttons);
        sendMessage(message.getUid(), WebSocketMessageType.ShowDialog, new ActionDialog("Title!", "Description!", ActionDialogType.Buttons, b));
    */}

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
            case ShowFieldInfo:
                sendFieldInfo(message);
                break;
            case PerformDialogActions:
                processDialogActions(message);
                break;
        }
    }

    private void processDialogActions(BasicMessage message) {
        log.info("processDialogAction: {}", message.getBody() );
        this.manager.get(this.state).performDialogAction(message);
    }

    private void sendFieldInfo(BasicMessage message) {
        log.info("Requested: ShowFieldInfo; {}", message.getBody());
        //sendMessage(message.getUid(), WebSocketMessageType.ShowFieldInfo, );
    }

    private void sendAllInfoRequest(BasicMessage message) {
        sendMessage(message.getUid(), WebSocketMessageType.PlayersList, players);
        sendMessage(message.getUid(), WebSocketMessageType.BoardGUI, this.board);

        this.players.forEach(p -> sendMessage(message.getUid(), WebSocketMessageType.AssignChip, p.getChip()));
        this.players.forEach(p -> sendMessage(message.getUid(), WebSocketMessageType.PlayerColor, new PlayerColor(p.getUserId(), p.getChip().getColor())));

        //sendMessage(message.getUid(), WebSocketMessageType.ChipMove, red);
    }

    public void sendMessage(Long userId, WebSocketMessageType type, Object o) {
        try {
            if (socks.get(userId).isOpen())
                socks.get(userId).sendMessage(
                        new TextMessage(
                                om.writeValueAsString(
                                        new ResponseMessage(type, om.writeValueAsString(o))
                                )
                        )
                );
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        while(true) {
            if (isGameLoopFrozen) continue;
            this.manager.get(this.state).performState();
        }
    }
}
