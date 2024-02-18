package com.alterdekim.game.service;

import com.alterdekim.game.entities.Chat;

import java.util.List;

public interface ChatService {
    List<Chat> getLastChats(Integer count);

    void sendChat(Chat chat);

    List<Chat> getAfterLastChatId(Long lastChatId);
}
