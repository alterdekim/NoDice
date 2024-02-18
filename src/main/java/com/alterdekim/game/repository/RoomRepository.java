package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}