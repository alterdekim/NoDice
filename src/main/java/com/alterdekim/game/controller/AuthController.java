package com.alterdekim.game.controller;

import com.alterdekim.game.dto.FriendPageResult;
import com.alterdekim.game.dto.UserDTO;
import com.alterdekim.game.entities.Invite;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.InviteService;
import com.alterdekim.game.service.InviteServiceImpl;
import com.alterdekim.game.service.UserService;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.AuthenticationUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class AuthController {

    private final String base_title = " | Nosedive";

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private InviteServiceImpl inviteService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        AuthenticationUtil.authProfile(model, userService);
        model.addAttribute("title", "Login" + base_title);
        return "login";
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        AuthenticationUtil.authProfile(model, userService);
        UserDTO userDto = new UserDTO();
        model.addAttribute("user", userDto);
        return "signup";
    }

    @PostMapping("/signup")
    public String registration(
            @ModelAttribute("user") @Valid UserDTO userDto,
            BindingResult result,
            Model model) {
        User existingUser = userService.findByUsername(userDto.getUsername());
        Invite existingInvite = inviteService.findById(1);

        if(existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty() ){
            result.rejectValue("username", null,
                    "There is already an account registered with the same username");
            return "redirect:/signup?error=already_exists";
        }

        if(!existingInvite.getInvite_code().equals(userDto.getInvite_code())) {
            result.rejectValue("invite_code", null, "Incorrect invite code.");
            return "redirect:/signup?error=bad_invite";
        }

        if(!userDto.getTerms_and_conditions_accept()) {
            result.rejectValue("terms_and_conditions_accept", null, "You must accept terms and conditions");
            return "redirect:/signup?error=terms_and_conditions";
        }

        if(result.hasErrors()) {
            model.addAttribute("user", new UserDTO());
            return "redirect:/signup?error=other";
        }

        userService.saveUser(userDto);
        return "redirect:/";
    }
}

