package com.alterdekim.game.service;

import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.repository.RoomPlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomPlayerServiceImpl implements RoomPlayerService{

    private final RoomPlayerRepository repository;

    public RoomPlayerServiceImpl(RoomPlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RoomPlayer> getAll() {
        return repository.findAll();
    }

    @Override
    public List<RoomPlayer> findByRoomId(Long roomId) {
        return repository.findByRoomId(roomId);
    }

    public void joinRoom(Long id, Long userId) {
        repository.save(new RoomPlayer(id, userId));
    }

    public void leaveByUserId(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    public Long hasUserId(Long userId) {
        return repository.hasUserId(userId);
    }

    public void removeByRoomId(Long roomId) {
        repository.removeByRoomId(roomId);
    }
}
