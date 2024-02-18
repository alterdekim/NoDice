package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "SELECT c FROM Chat c ORDER BY c.createdAt DESC LIMIT :count")
    List<Chat> getLastChats(@Param(value = "count") Integer count);

    @Query(value = "SELECT c FROM Chat c WHERE c.id > :lastChatId ORDER BY c.createdAt ASC")
    List<Chat> getAfterLastChatId(@Param(value = "lastChatId") Long lastChatId);
}
