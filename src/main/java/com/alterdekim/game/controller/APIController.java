package com.alterdekim.game.controller;


import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.component.LongPollConfig;
import com.alterdekim.game.component.LongPollingSession;
import com.alterdekim.game.dto.*;
import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.*;
import com.alterdekim.game.util.Hash;
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

    @GetMapping("/api/v1/chat/history/{count}/")
    public ResponseEntity<ChatResult> chatList(@PathVariable Integer count ) {
        List<Chat> results = chatService.getLastChats(count);
        results = results.stream().map( c -> {
            String message = c.getMessage();
            for( int i = 0; i < message.length(); i++ ) {
                if( message.charAt(i) == '@' ) {
                    int u = message.substring(i).indexOf(' ') + i;
                    String username = message.substring(i+1, u);
                    User user = userService.findByUsername(username);
                    if( user != null ) {
                        Long uid = user.getId();
                        message = message.substring(0, i) + "<a href=\"/profile/" + uid + "\" class=\"chat-history-user\"><span class=\"_nick\">" + username + "</span></a>" + message.substring(u + 1);
                    } else {
                        message = message.substring(0, i) + username + message.substring(u + 1);
                    }
                    i = 0;
                }
            }
            c.setMessage(message);
            return c;
        }).collect(Collectors.toList());
        List<UserResult> users = results.stream()
                .map(Chat::getUserId)
                .distinct()
                .map( l -> userService.findById(l) )
                .map( u -> new UserResult(u.getId(), u.getUsername()) )
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ChatResult(results, users));
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
                                              @RequestParam("players_count") Integer players_count) {
        if( !(players_count >= 2 && players_count <= 5) ) return ResponseEntity.badRequest().build();
        Long id = roomService.createRoom(new Room(players_count, is_private, "0"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername()).getId();
        roomPlayerService.leaveByUserId(userId);
        roomPlayerService.joinRoom(id, userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/async/notify/get/")
    @ResponseBody
    public DeferredResult<LongPollResult> getNotify(@RequestParam("last_chat_id") Long last_chat_id,
                                                    @RequestParam("accessToken") String accessToken,
                                                    @RequestParam("uid") Long userId,
                                                    @RequestParam("poll_token") String poll_token) {
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
        if( longPoll.getMap().containsKey(userId) ){
            LongPollConfig c = longPoll.getMap().get(userId);
            if( !c.getPoll_token().equals(poll_token) ) {
                c = new LongPollConfig(last_chat_id, new ArrayList<>(), 0, poll_token, new ArrayList<>(), System.currentTimeMillis());
                longPoll.getLongPollingQueue().removeIf(q -> q.getUserId().longValue() == userId.longValue());
            }
            c.setLast_chat_id(last_chat_id);
            c.setSession_pass(0);
            c.setLastRequest(System.currentTimeMillis());
            longPoll.getMap().put(userId, c);
        } else {
            longPoll.getMap().put(userId, new LongPollConfig(last_chat_id, new ArrayList<>(), 0, poll_token, new ArrayList<>(), System.currentTimeMillis()));
        }
        longPoll.getLongPollingQueue().add(new LongPollingSession(userId, deferredResult));
        return deferredResult;
    }
}
