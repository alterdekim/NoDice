package com.alterdekim.game.controller;

import com.alterdekim.game.dto.AuthApiObject;
import com.alterdekim.game.dto.FriendPageResult;
import com.alterdekim.game.dto.SelectOption;
import com.alterdekim.game.entities.Image;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.repository.ImageRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class StaticController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FriendServiceImpl friendService;

    @Autowired
    private ImageRepository imageRepository;

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
                .map(u -> new FriendPageResult("/profile/"+u.getId(), "background-image: url(\"/image/store/"+u.getAvatarId()+"\");", u.getUsername(), u.getId(), u.getDisplayName())));
        return "friends";
    }

    @GetMapping("/profile/{id}")
    public String profilePage(@PathVariable("id") Long id, Model model) {
        AuthenticationUtil.authProfile(model, userService);
        User u = userService.findById(id);
        model.addAttribute("page_user", new FriendPageResult("", "background-image: url(\"/image/store/"+u.getAvatarId()+"\");", u.getUsername(), u.getId(), u.getDisplayName()));
        return "profile";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        User u = AuthenticationUtil.authProfile(model, userService);
        List<SelectOption> options = new ArrayList<>();
        options.add(new SelectOption("she/her", true));
        options.add(new SelectOption("he/him", false));
        options.add(new SelectOption("they/them", false));
        options.add(new SelectOption("idc", false));
        String p = u.getPronouns();
        options = options.stream().map(o -> {
            if(p.equals(o.getText())) {
                return new SelectOption(o.getText(), true);
            }
            return new SelectOption(o.getText(), false);
        }).collect(Collectors.toList());
        model.addAttribute("options", options);
        return "settings";
    }

    @PostMapping(value = "/settings")
    private String uploadImage(@RequestParam(required = true, name = "file-0") MultipartFile multipartImage) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication.isAuthenticated() ) {
            try {
                User u = userService.findByUsername(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername());
                Image dbImage = new Image();
                dbImage.setContent(multipartImage.getBytes());
                userService.setAvatar(u.getId(), imageRepository.save(dbImage)
                        .getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return "redirect:/settings";
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
