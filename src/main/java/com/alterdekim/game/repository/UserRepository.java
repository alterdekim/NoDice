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
    @Query(value = "UPDATE User u SET u.avatarId = :avatarId WHERE u.id = :userId")
    void updateAvatar(@Param("userId") Long userId, @Param("avatarId") Long avatarId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.displayName = :displayName, u.pronouns = :pronouns, u.username = :username WHERE u.id = :userId")
    void updateProfileInfo(@Param("userId") Long userId,
                           @Param("displayName") String displayName,
                           @Param("username") String username,
                           @Param("pronouns") String pronouns);
}
