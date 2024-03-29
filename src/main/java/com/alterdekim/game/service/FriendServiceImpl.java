package com.alterdekim.game.service;

import com.alterdekim.game.dto.FriendFollowState;
import com.alterdekim.game.entities.FriendStatus;
import com.alterdekim.game.repository.FriendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FriendServiceImpl {
    private final FriendRepository repository;

    public List<Long> getFriendsOfUserId(Long userId) {
        return repository.getFriendsOfUserId(userId);
    }

    public List<Long> getFollowersOfUserId(Long userId) {
        return repository.getFollowersOfUserId(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        repository.removeFriend(userId, friendId);
    }

    public void followUser(Long userId, Long friendId) {
        if( repository.getFollow(userId, friendId) == null ) {
            repository.save(new FriendStatus(userId, friendId, 1));
            return;
        }
        repository.setFriend(userId, friendId);
    }

    public FriendStatus getFollow(Long userId, Long friendId) {
        return repository.getFollow(userId, friendId);
    }

    public FriendStatus getFriend(Long userId, Long friendId) {
        return repository.getFriend(userId, friendId);
    }

    public FriendFollowState getFriendState(Long userId, Long friendId) {
        FriendStatus fs = this.getFollow(userId, friendId);
        if( (fs != null && fs.getFirstUserId().longValue() == userId.longValue()) ||
            this.getFriend(userId, friendId) != null ) {
            return FriendFollowState.FOLLOWED;
        } else if( (fs != null && fs.getFirstUserId().longValue() != userId.longValue()) ) {
            return FriendFollowState.ACCEPT;
        }
        return FriendFollowState.NOT_FOLLOWED;
    }
}
