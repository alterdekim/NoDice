package com.alterdekim.game.controller;


import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.component.LongPollConfig;
import com.alterdekim.game.component.LongPollingSession;
import com.alterdekim.game.component.result.LongPollResult;
import com.alterdekim.game.dto.*;
import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.*;
import com.alterdekim.game.util.Hash;
import com.alterdekim.game.util.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class APIController {

    @Autowired
    private RoomServiceImpl roomService;

    @Autowired
    private RoomPlayerServiceImpl roomPlayerService;

    @Autowired
    private ChatServiceImpl chatService;

    @Autowired
    private BannerServiceImpl bannerService;

    @Autowired
    private TextDataValServiceImpl textDataValService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LongPoll longPoll;

    @Autowired
    private FriendServiceImpl friendService;

    @GetMapping("/api/v1/chat/history/{count}/")
    public ResponseEntity<List<ChatResult>> chatList(@PathVariable Integer count ) {
        List<Chat> results = chatService.getLastChats(count);
        return ResponseEntity.ok(results.stream()
                .peek(c -> c.setMessage(StringUtil.escapeTags(userService, c.getMessage())))
                .map(c -> {
                    User u1 = userService.findById(c.getUserId());
                    return new ChatResult(c, new UserResult( c.getUserId(), u1.getUsername(), u1.getAvatarId()));
                })
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/v1/market/banners/{lang}/")
    public ResponseEntity<List<Banner>> getBanners( @PathVariable String lang ) {
        List<Banner> banners = null;
        if( lang.equals("ru") ) {
            banners = bannerService.getAllActive()
                    .stream()
                    .map( b -> new Banner(b.getId(),
                            textDataValService.findById(b.getTitleId()).getTextRus(),
                            textDataValService.findById(b.getDescriptionId()).getTextRus(),
                            b.getGradientInfo(),
                            b.getImageUrl()))
                    .collect(Collectors.toList());
        } else {
            banners = bannerService.getAllActive()
                    .stream()
                    .map( b -> new Banner(b.getId(),
                            textDataValService.findById(b.getTitleId()).getTextEng(),
                            textDataValService.findById(b.getDescriptionId()).getTextEng(),
                            b.getGradientInfo(),
                            b.getImageUrl()))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(banners);
    }

    @PostMapping("/api/v1/chat/send")
    public ResponseEntity<String> sendChat( @RequestBody String message ) {
        try {
            message = URLDecoder.decode(message, "UTF-8");
            message = message.substring(0, message.length() - 1);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
            chatService.sendChat(new Chat(userId, message, System.currentTimeMillis() / 1000L));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/api/v1/rooms/create/")
    public ResponseEntity<String> createRoom( @RequestParam("is_private") Boolean is_private,
                                              @RequestParam("players_count") Integer players_count ) {
        if( !(players_count >= 2 && players_count <= 5) ) return ResponseEntity.badRequest().build();
        Long id = roomService.createRoom(new Room(players_count, is_private, "0"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        roomPlayerService.leaveByUserId(userId);
        roomPlayerService.joinRoom(id, userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/api/v1/rooms/join/")
    public ResponseEntity<String> joinRoom( @RequestParam("room_id") Long roomId ) {
        if( !roomService.findById(roomId).isPresent() ) return ResponseEntity.badRequest().build();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        roomPlayerService.leaveByUserId(userId);
        roomPlayerService.joinRoom(roomId, userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/api/v1/rooms/invite/")
    public ResponseEntity<String> inviteToRoom( @RequestParam("friend_id") Long friend_id ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        if( friendService.getFriendsOfUserId(userId).stream().anyMatch( p -> p.longValue() == friend_id.longValue()) &&
            roomPlayerService.hasUserId(userId) != null) {
            LongPollConfig config = longPoll.getMap().get(friend_id);
            if( config != null ) {
                List<GameInvite> l = config.getInvites();
                Long roomId = roomPlayerService.hasUserId(userId);
                l.add(new GameInvite(roomId, userId, ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()));
                config.setInvites(l);
                longPoll.getMap().put(friend_id, config);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/api/v1/friends/remove/")
    public ResponseEntity<String> removeFriend( @RequestParam("friend_id") Long friend_id ) {
        if( userService.findById(friend_id) == null ) return ResponseEntity.badRequest().build();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        friendService.removeFriend(userId, friend_id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/settings/profile/save")
    public ResponseEntity<String> saveProfile( @RequestParam("display_name") String displayName,
                                               @RequestParam("nickname") String nickname,
                                               @RequestParam("pronouns") String pronouns ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        userService.updateProfileInfo(userId, displayName, nickname, pronouns);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/friends/follow")
    public ResponseEntity<String> followFriend( @RequestParam("userId") Long friendId ) {
        if( userService.findById(friendId) == null ) return ResponseEntity.badRequest().build();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        friendService.followUser(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/async/notify/get/")
    @ResponseBody
    public DeferredResult<LongPollResult> getNotify(@RequestParam("last_chat_id") Long last_chat_id,
                                                    @RequestParam("accessToken") String accessToken,
                                                    @RequestParam("uid") Long userId,
                                                    @RequestParam("poll_token") String poll_token,
                                                    @RequestParam("rids") String rooms_str,
                                                    @RequestParam("fids") String friends_str) {
        try {
            User u = userService.findById(userId);
            if (u == null) return null;
            if (!Hash.sha256((u.getId() + u.getUsername() + u.getPassword()).getBytes()).equals(accessToken))
                return null;
        } catch ( Exception e ) {
            log.error(e.getMessage(), e);
        }
        if( poll_token.length() != 32 ) return null;
        final DeferredResult<LongPollResult> deferredResult = new DeferredResult<>();
        List<RoomResult> rooms = new ArrayList<>();
        List<UserResult> friends = new ArrayList<>();
        try {
            rooms = new ObjectMapper().readValue(rooms_str, new TypeReference<List<RoomResult>>() {});
            friends = new ObjectMapper().readValue(friends_str, new TypeReference<List<UserResult>>() {});
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if( longPoll.getMap().containsKey(userId) ){
            LongPollConfig c = longPoll.getMap().get(userId);
            if( !c.getPoll_token().equals(poll_token) ) {
                c = new LongPollConfig(last_chat_id, rooms, 0, poll_token, friends, System.currentTimeMillis(), new ArrayList<>(), new ArrayList<>());
                longPoll.getLongPollingQueue().removeIf(q -> q.getUserId().longValue() == userId.longValue());
            }
            c.setRooms(rooms);
            c.setFriends_online(friends);
            c.setLast_chat_id(last_chat_id);
            c.setSession_pass(0);
            c.setLastRequest(System.currentTimeMillis());
            longPoll.getMap().put(userId, c);
        } else {
            longPoll.getMap().put(userId, new LongPollConfig(last_chat_id, rooms, 0, poll_token, friends, System.currentTimeMillis(), new ArrayList<>(), new ArrayList<>()));
        }
        longPoll.getLongPollingQueue().add(new LongPollingSession(userId, deferredResult));
        return deferredResult;
    }
}
