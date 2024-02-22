package com.alterdekim.game.component;

import com.alterdekim.game.dto.*;
import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.*;
import com.alterdekim.game.util.Hash;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    @Scheduled(fixedRate = 3000)
    private void longPoll() {
        getLongPollingQueue().removeIf(e -> e.getDeferredResult().isSetOrExpired());
        roomService.clearEmptyRooms();
        getMap().keySet().forEach(k -> {
            if( (System.currentTimeMillis() - getMap().get(k).getLastRequest()) >= 60000 ) getMap().remove(k);
        });
        getLongPollingQueue().forEach(longPollingSession -> {
            try {
                if( !map.containsKey(longPollingSession.getUserId())) map.put(longPollingSession.getUserId(), new LongPollConfig(0L,new ArrayList<>(), 0, Hash.rnd(), new ArrayList<>(), System.currentTimeMillis()));
                LongPollConfig config = map.get(longPollingSession.getUserId());
                LongPollResult result = process(longPollingSession.getUserId(), config);
                if( !result.getRooms().isEmpty() )
                    config.setRooms(result.getRooms().stream().filter(r -> r.getAction() == RoomResultState.ADD_CHANGE).map(r -> new RoomResult(r.getId(), r.getPlayerCount(), r.getPlayers())).collect(Collectors.toList()));
                if( !result.getFriends().isEmpty() )
                    config.setFriends_online(result.getFriends().stream().filter(r -> r.getAction() == FriendState.ADD).map(r -> new UserResult(r.getId(), r.getUsername())).collect(Collectors.toList()));

                config.setSession_pass(config.getSession_pass()+1);

                if( !result.getFriends().isEmpty() || !result.getRooms().isEmpty() || !result.getMessages().isEmpty() || config.getSession_pass() >= iterations) {
                    longPollingSession.getDeferredResult().setResult(result);
                    config.setSession_pass(0);
                }
                map.put(longPollingSession.getUserId(), config);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        getLongPollingQueue().removeIf(e -> e.getDeferredResult().isSetOrExpired());
    }

    private LongPollResult process(Long userId, LongPollConfig config) {
        Integer onlineCount = map.size();
        List<Chat> results;
        List<UserResult> users = new ArrayList<>();
        List<RoomResult> clientRooms = config.getRooms();
        List<RoomResultV2> roomResults = new ArrayList<>();
        List<UserResult> clientFriends = config.getFriends_online();
        List<FriendResult> friendsResult = new ArrayList<>();

        results = chatService.getAfterLastChatId(config.getLast_chat_id());

        // Chat part
        if( !results.isEmpty() ) {
            users = results.stream()
                    .map(Chat::getUserId)
                    .distinct()
                    .map(l -> userService.findById(l))
                    .map(u -> new UserResult(u.getId(), u.getUsername()))
                    .collect(Collectors.toList());
            results = results.stream().peek(c -> {
                String message = c.getMessage();
                for (int i = 0; i < message.length(); i++) {
                    if (message.charAt(i) == '@') {
                        int u = message.substring(i).indexOf(' ') + i;
                        String username = message.substring(i + 1, u);
                        User user = userService.findByUsername(username);
                        if (user != null) {
                            Long uid = user.getId();
                            message = message.substring(0, i) + "<a href=\"/profile/" + uid + "\" class=\"chat-history-user\"><span class=\"_nick\">" + username + "</span></a>" + message.substring(u + 1);
                        } else {
                            message = message.substring(0, i) + username + message.substring(u + 1);
                        }
                        i = 0;
                    }
                }
                c.setMessage(message);
            }).collect(Collectors.toList());
        }

        // Rooms part
        if( !clientRooms.isEmpty() ) {
            List<RoomResult> rooms = roomService.getAllActive().stream()
                    .map( r -> new RoomResult(r.getId(), r.getPlayerCount(), roomPlayerService.findByRoomId(r.getId()).stream()
                            .map(p -> userService.findById(p.getUserId()))
                            .map(p -> new UserResult(p.getId(), p.getUsername()))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
            if( !isEqualListRoom(rooms, clientRooms) ) {
                List<RoomResult> rr = new ArrayList<>(rooms);
                List<RoomResultV2> resultV2List = new ArrayList<>();
                for( RoomResult r : clientRooms ) {
                    if( rooms.stream().noneMatch(t -> t.getId().longValue() == r.getId().longValue()) ) {
                        resultV2List.add(new RoomResultV2(RoomResultState.REMOVE, r.getId(), r.getPlayerCount(), r.getPlayers()));
                    } else {
                        RoomResult r1 = rooms.stream().filter( t -> t.getId().longValue() == r.getId().longValue()).findFirst().get();
                        if( !isEqual(r, r1) ) {
                            resultV2List.add(new RoomResultV2(RoomResultState.ADD_CHANGE, r1.getId(), r1.getPlayerCount(), r1.getPlayers()));
                        }
                        rr.remove(r1);
                    }
                }
                for( RoomResult r : rr ) {
                    resultV2List.add(new RoomResultV2(RoomResultState.ADD_CHANGE, r.getId(), r.getPlayerCount(), r.getPlayers()));
                }
                roomResults = resultV2List.stream().sorted(Comparator.comparingLong(RoomResultV2::getId)).collect(Collectors.toList());
            } else {
                roomResults = new ArrayList<>();
            }
        } else {
            List<Room> rooms = roomService.getAllActive();
            roomResults = rooms.stream()
                    .map( r -> new RoomResultV2(RoomResultState.ADD_CHANGE, r.getId(), r.getPlayerCount(), roomPlayerService.findByRoomId(r.getId()).stream()
                            .map(p -> userService.findById(p.getUserId()))
                            .map(p -> new UserResult(p.getId(), p.getUsername()))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }

        // Friends part
        if( !clientFriends.isEmpty() ) {
            List<UserResult> userResults = friendService.getFriendsOfUserId(userId)
                    .stream()
                    .map(id -> userService.findById(id))
                    .map(u -> new UserResult(u.getId(), u.getUsername()))
                    .filter(f -> getMap().keySet().stream().anyMatch(l -> l.longValue() == f.getId().longValue()))
                    .collect(Collectors.toList());

            if( !isEqualListUser(userResults, clientFriends) ) {
                List<UserResult> urr = new ArrayList<>(userResults);
                List<FriendResult> fr = new ArrayList<>();
                for( UserResult r : clientFriends ) {
                    if( userResults.stream().noneMatch(t -> t.getId().longValue() == r.getId().longValue()) ) {
                        fr.add(new FriendResult(FriendState.REMOVE, r.getId(), r.getUsername()));
                    } else {
                        UserResult r1 = userResults.stream().filter( t -> t.getId().longValue() == r.getId().longValue()).findFirst().get();
                        if( !isEqualUser(r, r1) ) {
                            fr.add(new FriendResult(FriendState.ADD, r1.getId(), r1.getUsername()));
                        }
                        urr.remove(r1);
                    }
                }
                for( UserResult r : urr ) {
                    fr.add(new FriendResult(FriendState.ADD, r.getId(), r.getUsername()));
                }
                friendsResult = fr.stream().sorted(Comparator.comparingLong(FriendResult::getId)).collect(Collectors.toList());
            } else {
                friendsResult = new ArrayList<>();
            }
        } else {
            friendsResult = friendService.getFriendsOfUserId(userId)
                    .stream()
                    .map(id -> userService.findById(id))
                    .filter(f -> getMap().keySet().stream().anyMatch(l -> l.longValue() == f.getId().longValue()))
                    .map(f -> new FriendResult(FriendState.ADD, f.getId(), f.getUsername()))
                    .collect(Collectors.toList());
        }

        return new LongPollResult(onlineCount, results, users, roomResults, friendsResult);
    }

    private Boolean isEqual(RoomResult r1, RoomResult r2) {
        return r1.getId().longValue() == r2.getId().longValue() &&
                r1.getPlayerCount().intValue() == r2.getPlayerCount().intValue() &&
                isEqualListUser(r1.getPlayers(), r2.getPlayers());
    }

    private Boolean isEqualUser(UserResult r1, UserResult r2) {
        return r1.getId().longValue() == r2.getId().longValue() &&
                r1.getUsername().equals(r2.getUsername());
    }

    private Boolean isEqualListRoom(List<RoomResult> r1, List<RoomResult> r2) {
        r1 = r1.stream().sorted(Comparator.comparingLong(RoomResult::getId)).collect(Collectors.toList());
        r2 = r2.stream().sorted(Comparator.comparingLong(RoomResult::getId)).collect(Collectors.toList());
        if( r1.size() != r2.size() ) return false;
        for( int i = 0; i < r1.size(); i++ ) {
            if( !isEqual(r1.get(i), r2.get(i)) ) return false;
        }
        return true;
    }

    private Boolean isEqualListUser(List<UserResult> r1, List<UserResult> r2) {
        r1 = r1.stream().sorted(Comparator.comparingLong(UserResult::getId)).collect(Collectors.toList());
        r2 = r2.stream().sorted(Comparator.comparingLong(UserResult::getId)).collect(Collectors.toList());
        if( r1.size() != r2.size() ) return false;
        for( int i = 0; i < r1.size(); i++ ) {
            if( r1.get(i).getId().longValue() != r2.get(i).getId().longValue() ||
                    !r1.get(i).getUsername().equals(r2.get(i).getUsername()) ) {
                return false;
            }
        }
        return true;
    }
}
