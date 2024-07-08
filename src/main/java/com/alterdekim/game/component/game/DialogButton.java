package com.alterdekim.game.component.game;

import com.alterdekim.game.websocket.message.WebSocketMessageType;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DialogButton {
    private String buttonText;
    private DialogButtonColor buttonColor;
    private List<WebSocketMessageType> onclickAction;
}
