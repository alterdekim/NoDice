package com.alterdekim.game.service;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;

import java.util.List;

public interface RoomPlayerService {
    List<RoomPlayer> getAll();

    List<RoomPlayer> findByRoomId(Long roomId);
}
