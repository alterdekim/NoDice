package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.TextDataVal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextDataValRepository extends JpaRepository<TextDataVal, Long>  {
    Optional<TextDataVal> findById(Long id);
}
