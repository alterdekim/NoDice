package com.alterdekim.game.controller;

import com.alterdekim.game.dto.UserDTO;
import com.alterdekim.game.entities.Invite;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.InviteService;
import com.alterdekim.game.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    private final UserService userService;
    private final InviteService inviteService;

    public AuthController(UserService userService, InviteService inviteService) {
        this.inviteService = inviteService;
        this.userService = userService;
    }

    @GetMapping("/game")
    public String gamePage(Model model) {
        return "game";
    }

    @GetMapping("/rules")
    public String rulesPage(Model model) {
        return "rules";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Login" + base_title);
        return "login";
    }

    @GetMapping("/games")
    public String gamesPage(Model model) {
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

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
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

