package com.alterdekim.game.component.game;

import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.websocket.message.BasicMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GameRoom {

    @Getter
    private List<RoomPlayer> players;

    private ConcurrentHashMap<Long, WebSocketSession> socks;

    public GameRoom(List<RoomPlayer> players) {
        this.players = players;
        this.socks = new ConcurrentHashMap<>();
    }

    public void receiveMessage(BasicMessage message, WebSocketSession session) {
        if(players.stream().noneMatch(p -> p.getUserId().longValue() == message.getUid().longValue())) return;
        socks.put(message.getUid(), session);
        log.info("GOT MESSAGE " + message.getUid() + " " + message.getAccessToken() + " " + message.getBody());
        this.sendMessage(message.getUid());
    }

    private void sendMessage(Long userId) {
        try {
            if (socks.get(userId).isOpen())
                socks.get(userId).sendMessage(new TextMessage("HEY!"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
