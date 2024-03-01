package com.alterdekim.game.service;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public List<Room> getAllActive() {
        return roomRepository.findAllByActiveTrue();
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public Long createRoom(Room room) {
        return roomRepository.save(room).getId();
    }

    public void clearEmptyRooms() {
        roomRepository.clearEmptyRooms();
    }

    public void removeRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }
}
