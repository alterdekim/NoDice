package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InviteResult {
    private Long roomId;
    private Long userId;
    private String username;
}
