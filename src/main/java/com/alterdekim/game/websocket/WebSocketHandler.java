package com.alterdekim.game.websocket;

import com.alterdekim.game.component.game.GamePool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;


@Slf4j
@AllArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private GamePool gamePool;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        String receivedMessage = (String) message.getPayload();
        gamePool.receiveMessage(receivedMessage, session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Perform actions when a new WebSocket connection is established
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Perform actions when a WebSocket connection is closed
    }
}