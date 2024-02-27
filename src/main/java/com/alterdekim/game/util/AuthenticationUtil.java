package com.alterdekim.game.util;

import com.alterdekim.game.dto.FriendPageResult;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

@Slf4j
public class AuthenticationUtil {
    public static User authProfile(Model model, UserServiceImpl userService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication.isAuthenticated() ) {
            try {
                User u = userService.findByUsername(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername());
                model.addAttribute("profile", new FriendPageResult("/profile/" + u.getId(), "/image/store/"+u.getAvatarId(), u.getUsername(), u.getId(), u.getDisplayName()));
                return u;
            } catch (Exception e) {
               // log.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
