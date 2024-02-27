package com.alterdekim.game.repository;

import com.alterdekim.game.entities.FriendStatus;
import com.alterdekim.game.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<FriendStatus, Long> {
    @Query(value = "SELECT IF(f.firstUserId = :userId, f.secondUserId, f.firstUserId) FROM FriendStatus f WHERE (f.firstUserId = :userId OR f.secondUserId = :userId) AND f.status = 2")
    List<Long> getFriendsOfUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM FriendStatus f WHERE ((f.firstUserId = :userId AND f.secondUserId = :friendId) OR (f.firstUserId = :friendId AND f.secondUserId = :userId)) AND f.status = 2")
    void removeFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query(value = "SELECT f FROM FriendStatus f WHERE ((f.firstUserId = :userId AND f.secondUserId = :friendId) OR (f.firstUserId = :friendId AND f.secondUserId = :userId)) AND f.status = 1")
    FriendStatus getFollow(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE FriendStatus f SET f.status = 2 WHERE ((f.firstUserId = :userId AND f.secondUserId = :friendId) OR (f.firstUserId = :friendId AND f.secondUserId = :userId)) AND f.status = 1")
    void setFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
}