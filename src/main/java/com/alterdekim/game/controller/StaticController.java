package com.alterdekim.game.controller;

import com.alterdekim.game.dto.AuthApiObject;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.UserServiceImpl;
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

    @GetMapping("/rules")
    public String rulesPage(Model model) {
        return "rules";
    }

    @GetMapping("/games")
    public String gamesPage(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User u = userService.findByUsername(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername());
            Long userId = u.getId();
            String apiKey = Hash.sha256((u.getId() + u.getUsername() + u.getPassword()).getBytes());
            model.addAttribute("auth_obj", new AuthApiObject(apiKey, userId, Hash.rnd()));
        } catch ( Exception e ) {
            log.error(e.getMessage(), e);
        }
        return "games";
    }

    @GetMapping("/")
    public String homePage(Model model) {
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("title", "Access denied");
        return "access-denied";
    }
}
