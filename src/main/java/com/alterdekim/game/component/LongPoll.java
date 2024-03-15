package com.alterdekim.game.component;

import com.alterdekim.game.component.game.GamePool;
import com.alterdekim.game.component.processors.ChatProcessor;
import com.alterdekim.game.component.processors.FriendProcessor;
import com.alterdekim.game.component.processors.Processor;
import com.alterdekim.game.component.processors.RoomProcessor;
import com.alterdekim.game.component.result.LongPollResult;
import com.alterdekim.game.component.result.LongPollResultSingle;
import com.alterdekim.game.component.result.LongPollResultType;
import com.alterdekim.game.dto.*;
import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Getter
@Slf4j
public class LongPoll {

    private final Integer iterations = 6;

    private final BlockingQueue<LongPollingSession> longPollingQueue = new ArrayBlockingQueue<>(100);
    private final ConcurrentHashMap<Long, LongPollConfig> map = new ConcurrentHashMap<>();

    private final List<Processor> processors = new ArrayList<>();

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ChatServiceImpl chatService;

    @Autowired
    private RoomServiceImpl roomService;

    @Autowired
    private RoomPlayerServiceImpl roomPlayerService;

    @Autowired
    private FriendServiceImpl friendService;

    @Autowired
    private GamePool gamePool;

    public LongPoll() {
        processors.addAll(Arrays.asList(new ChatProcessor(this), new FriendProcessor(this), new RoomProcessor(this)));
    }

    @Scheduled(fixedRate = 3000)
    private void longPoll() {
        getLongPollingQueue().removeIf(e -> e.getDeferredResult().isSetOrExpired());
        roomService.clearEmptyRooms();
        getMap().keySet().forEach(k -> {
            if( (System.currentTimeMillis() - getMap().get(k).getLastRequest()) >= 55000 ) getMap().remove(k);
        });
        roomService.getAll().forEach(r -> {
            roomPlayerService.findByRoomId(r.getId()).stream()
                    .filter(p -> !map.containsKey(p.getUserId()))
                    .forEach(p -> roomPlayerService.leaveByUserId(p.getUserId()));
        });
        getLongPollingQueue().forEach(longPollingSession -> {
            try {
                if( !map.containsKey(longPollingSession.getUserId())) map.put(longPollingSession.getUserId(), new LongPollConfig(0L));
                LongPollConfig config = map.get(longPollingSession.getUserId());
                LongPollResult result = process(longPollingSession.getUserId(), config);
                config.setSession_pass(config.getSession_pass()+1);
                if( !config.getGameRedirect().isEmpty() ||
                        !result.getResultWithType(LongPollResultType.InviteResult, InviteResult.class).isEmpty() ||
                        !result.getResultWithType(LongPollResultType.FriendResult, FriendResult.class).isEmpty() ||
                        !result.getResultWithType(LongPollResultType.RoomResult, RoomResultV2.class).isEmpty() ||
                        !result.getResultWithType(LongPollResultType.ChatResult, ChatResult.class).isEmpty() ||
                        config.getSession_pass() >= iterations) {
                    longPollingSession.getDeferredResult().setResult(result);
                    config.setSession_pass(0);
                    config.setInvites(new ArrayList<>());
                    config.setGameRedirect(new ArrayList<>());
                }
                map.put(longPollingSession.getUserId(), config);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        getLongPollingQueue().removeIf(e -> e.getDeferredResult().isSetOrExpired());
    }

    private LongPollResult process(Long userId, LongPollConfig config) {
        if( gamePool.containsPlayer(userId) ) config.getGameRedirect().add(new GameRedirect(gamePool.getGameIdByPlayerId(userId).get()));
        List<LongPollResultSingle> result = new ArrayList<>();
        result.add(new LongPollResultSingle<>(LongPollResultType.OnlineUsers, Arrays.asList(map.size())));
        result.add(new LongPollResultSingle<>(LongPollResultType.Redirect, config.getGameRedirect()));
        processors.forEach(p -> result.add(p.process(config, userId)));
        result.add(new LongPollResultSingle<>(LongPollResultType.InviteResult, config.getInvites().stream().map(i -> new InviteResult(i.getRoomId(), i.getUserId(), i.getUsername())).collect(Collectors.toList())));
        return new LongPollResult(result);
    }

    public void notifyPlayers(Long roomId, List<RoomPlayer> players) {
        players.forEach(p -> {
                    LongPollConfig lc = map.get(p.getUserId());
                    List<GameRedirect> gr = lc.getGameRedirect();
                    gr.add(new GameRedirect(roomId));
                    lc.setGameRedirect(gr);
                    map.put(p.getUserId(), lc);
                });
    }
}
