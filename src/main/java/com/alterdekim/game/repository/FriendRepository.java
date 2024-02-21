package com.alterdekim.game.repository;

import com.alterdekim.game.entities.FriendStatus;
import com.alterdekim.game.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<FriendStatus, Long> {
    @Query(value = "SELECT IF(f.firstUserId = :userId, f.secondUserId, f.firstUserId) FROM FriendStatus f WHERE (f.firstUserId = :userId OR f.secondUserId = :userId) AND f.status = 2")
    List<Long> getFriendsOfUserId(@Param("userId") Long userId);
}