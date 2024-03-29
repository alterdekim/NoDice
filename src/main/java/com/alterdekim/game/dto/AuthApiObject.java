package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthApiObject {
    private String accessToken;
    private Long uid;
    private String poll_token;
}
