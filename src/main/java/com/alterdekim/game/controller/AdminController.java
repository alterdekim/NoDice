package com.alterdekim.game.controller;

import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.AuthenticationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AdminController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        User u = AuthenticationUtil.authProfile(model, userService);
        if( !u.getRoles().get(0).getName().equals("ROLE_ADMINISTRATOR") ) {
            return "redirect:/games";
        }
        return "admin";
    }
}