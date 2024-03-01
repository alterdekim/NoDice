package com.alterdekim.game.controller;

import com.alterdekim.game.dto.AuthApiObject;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.AuthenticationUtil;
import com.alterdekim.game.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class GameController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/game")
    public String gamePage(Model model, @RequestParam("id") Long roomId) {
        model.addAttribute("roomId", roomId);
        try {
            User u = AuthenticationUtil.authProfile(model, userService);
            String apiKey = Hash.sha256((u.getId() + u.getUsername() + u.getPassword() + roomId).getBytes());
            model.addAttribute("auth_obj", new AuthApiObject(apiKey, u.getId(), Hash.rnd()));
        } catch ( Exception e ) {
            log.error(e.getMessage(), e);
        }
        return "game";
    }
}