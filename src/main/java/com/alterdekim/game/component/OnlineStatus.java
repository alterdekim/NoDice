package com.alterdekim.game.component;

import com.alterdekim.game.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OnlineStatus {

    @Autowired
    private UserServiceImpl userService;

    @Scheduled(fixedRate = 50000)
    private void resetOnlineStatus() {
        userService.setAllOffline();
    }
}
