package com.alterdekim.game.service;

import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public List<Chat> getLastChats(Integer count) {
        return chatRepository.getLastChats(count);
    }

    @Override
    public void sendChat(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public List<Chat> getAfterLastChatId(Long lastChatId) {
        return chatRepository.getAfterLastChatId(lastChatId);
    }
}
