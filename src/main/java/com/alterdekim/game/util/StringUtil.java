package com.alterdekim.game.util;

import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.UserServiceImpl;

public class StringUtil {
    public static String escapeTags(UserServiceImpl userService, String message) {
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '@') {
                int u = message.substring(i).indexOf(' ') + i;
                if( u <= (i + 1) || u > message.length() ) return message;
                String username = message.substring(i + 1, u);
                User user = userService.findByUsername(username);
                if (user != null) {
                    Long uid = user.getId();
                    message = message.substring(0, i) + "<a href=\"/profile/" + uid + "\" class=\"chat-history-user\"><span class=\"_nick\">" + username + "</span></a>" + message.substring(u + 1);
                } else {
                    message = message.substring(0, i) + username + message.substring(u + 1);
                }
                i = 0;
            }
        }
        return message;
    }
}
