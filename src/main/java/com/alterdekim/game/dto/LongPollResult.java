package com.alterdekim.game.dto;

import com.alterdekim.game.entities.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LongPollResult {
    private Integer onlineCount;
    private List<Chat> messages;
    private List<UserResult> users;
}
