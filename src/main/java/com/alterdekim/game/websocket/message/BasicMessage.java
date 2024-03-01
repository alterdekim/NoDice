package com.alterdekim.game.websocket.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicMessage {
    private Long roomId;
    private String accessToken;
    private Long uid;
    private String body;
}
