package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "SELECT r FROM Room r WHERE r.isPrivate = false ORDER BY r.id ASC")
    List<Room> findAllByActiveTrue();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query(value = "DELETE FROM room WHERE room.id NOT IN (SELECT t.room_id FROM (SELECT COUNT(id) as UID, room_id FROM room_player GROUP BY room_id) t)", nativeQuery = true)
    void clearEmptyRooms();
}