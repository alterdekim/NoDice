package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {
    List<RoomPlayer> findByRoomId(Long roomId);
}
