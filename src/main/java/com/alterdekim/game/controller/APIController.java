package com.alterdekim.game.controller;


import com.alterdekim.game.dto.*;
import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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

    @GetMapping("/api/v1/games/list")
    public ResponseEntity<List<RoomResult>> gamesList() {
        List<RoomResult> results = roomService.getAll()
                .stream()
                .map(room -> new RoomResult(room, roomPlayerService.findByRoomId(room.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

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

    @GetMapping("/api/v1/notify/get/{last_chat_id}/")
    public ResponseEntity<LongPollResult> getNotify(@PathVariable Long last_chat_id ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findByUsername(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername()).getId();
        userService.updateOnline(userId);

        Integer onlineCount = userService.countByIsOnline();
        List<Chat> results = chatService.getAfterLastChatId(last_chat_id);
        List<UserResult> users = results.stream()
                .map(Chat::getUserId)
                .distinct()
                .map( l -> userService.findById(l) )
                .map( u -> new UserResult(u.getId(), u.getUsername()) )
                .collect(Collectors.toList());
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
        return ResponseEntity.ok(new LongPollResult(onlineCount, results, users));
    }
}
