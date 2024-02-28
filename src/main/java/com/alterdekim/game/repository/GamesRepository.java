package com.alterdekim.game.repository;

import com.alterdekim.game.entities.FriendStatus;
import com.alterdekim.game.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<Game, Long> {

    @Query(value = "SELECT COUNT(g) FROM Game g WHERE g.userId = :userId")
    Long getGamesCount(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(g) FROM Game g WHERE g.userId = :userId AND g.isWon = true")
    Long getWonGamesCount(@Param("userId") Long userId);
}
