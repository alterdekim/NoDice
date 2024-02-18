package com.alterdekim.game.repository;

import com.alterdekim.game.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.isOnline = true WHERE u.id = :uuid")
    void setOnline(@Param(value = "uuid") Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.isOnline = false")
    void setAllOffline();

    Integer countByIsOnline(boolean isOnline);
}
