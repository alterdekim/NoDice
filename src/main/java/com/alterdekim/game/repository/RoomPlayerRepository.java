package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {

    @Query(value = "SELECT new RoomPlayer(r.id, r.roomId, r.userId) FROM RoomPlayer r WHERE r.roomId = :roomId ORDER BY r.userId ASC")
    List<RoomPlayer> findByRoomId(@Param("roomId") Long roomId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM RoomPlayer r WHERE r.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM RoomPlayer r WHERE r.roomId = :roomId")
    void removeByRoomId(@Param("roomId") Long roomId);

    @Query(value = "SELECT r.roomId FROM RoomPlayer r WHERE r.userId = :userId ORDER BY r.roomId ASC LIMIT 1")
    Long hasUserId(@Param("userId") Long userId);
}
