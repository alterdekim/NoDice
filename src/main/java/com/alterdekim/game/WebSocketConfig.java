package com.alterdekim.game;

import com.alterdekim.game.component.game.GamePool;
import com.alterdekim.game.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
@ComponentScan("com.alterdekim.game.component.game")
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private GamePool gamePool;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(gamePool), "/websocket")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }
}