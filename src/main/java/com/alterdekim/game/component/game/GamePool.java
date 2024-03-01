package com.alterdekim.game.component.game;

import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.RoomPlayerServiceImpl;
import com.alterdekim.game.service.RoomServiceImpl;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.Hash;
import com.alterdekim.game.websocket.message.BasicMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GamePool {

    @Autowired
    private LongPoll longPoll;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoomServiceImpl roomService;

    @Autowired
    private RoomPlayerServiceImpl roomPlayerService;

    private ConcurrentHashMap<Long, GameRoom> games = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 1000)
    private void refreshRooms() {
        roomService.getAllActive()
                .forEach(r -> {
                    if( roomPlayerService.findByRoomId(r.getId()).size() != r.getPlayerCount().intValue() ) return;
                    List<RoomPlayer> players = roomPlayerService.findByRoomId(r.getId());
                    roomPlayerService.removeByRoomId(r.getId());
                    roomService.removeRoom(r.getId());
                    games.put(r.getId(), new GameRoom(players));
                    longPoll.notifyPlayers(r.getId(), players);
                });
    }

    public void receiveMessage(String message, WebSocketSession session) {
        try {
            message = message.substring(message.indexOf("{"));
            BasicMessage pm = new ObjectMapper().readValue(message, BasicMessage.class);
            User u = userService.findById(pm.getUid());
            if (u == null || !games.containsKey(pm.getRoomId())) return;
            if (!Hash.sha256((u.getId() + u.getUsername() + u.getPassword() + pm.getRoomId()).getBytes()).equals(pm.getAccessToken()))
                return;
            games.get(pm.getRoomId()).receiveMessage(pm, session);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } catch (StringIndexOutOfBoundsException e) {
            //log.error(e.getMessage(), e);
        }
    }
}
