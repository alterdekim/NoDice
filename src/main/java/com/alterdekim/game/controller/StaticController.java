package com.alterdekim.game.controller;

import com.alterdekim.game.dto.AuthApiObject;
import com.alterdekim.game.dto.FriendPageResult;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.FriendServiceImpl;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.AuthenticationUtil;
import com.alterdekim.game.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class StaticController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FriendServiceImpl friendService;

    @GetMapping("/rules")
    public String rulesPage(Model model) {
        AuthenticationUtil.authProfile(model, userService);
        return "rules";
    }

    @GetMapping("/game")
    public String gamePage(Model model) {
        return "game";
    }

    @GetMapping("/games")
    public String gamesPage(Model model) {
        try {
            User u = AuthenticationUtil.authProfile(model, userService);
            String apiKey = Hash.sha256((u.getId() + u.getUsername() + u.getPassword()).getBytes());
            model.addAttribute("auth_obj", new AuthApiObject(apiKey, u.getId(), Hash.rnd()));
        } catch ( Exception e ) {
            log.error(e.getMessage(), e);
        }
        return "games";
    }

    @GetMapping("/friends")
    public String friendsPage(Model model) {
        Long userId = AuthenticationUtil.authProfile(model, userService).getId();
        model.addAttribute("friends", friendService.getFriendsOfUserId(userId).stream()
                .map(l -> userService.findById(l))
                .map(u -> new FriendPageResult("/profile/"+u.getId(), "background-image: url(&quot;https://i.dogecdn.wtf/wxEpEiovduvcXadQ&quot;);", u.getUsername(), u.getId())));
        return "friends";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        Long userId = AuthenticationUtil.authProfile(model, userService).getId();
        return "settings";
    }

    @GetMapping("/")
    public String homePage(Model model) {
        AuthenticationUtil.authProfile(model, userService);
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        AuthenticationUtil.authProfile(model, userService);
        model.addAttribute("title", "Access denied");
        return "access-denied";
    }
}
