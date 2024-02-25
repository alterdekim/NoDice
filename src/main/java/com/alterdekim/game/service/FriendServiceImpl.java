package com.alterdekim.game.service;

import com.alterdekim.game.repository.FriendRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendServiceImpl {
    private final FriendRepository repository;

    public List<Long> getFriendsOfUserId(Long userId) {
        return repository.getFriendsOfUserId(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        repository.removeFriend(userId, friendId);
    }
}
