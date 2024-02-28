package com.alterdekim.game.service;

import com.alterdekim.game.repository.GamesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GamesService {
    private final GamesRepository repository;

    public Long getGamesCount(Long userId) {
        return repository.getGamesCount(userId);
    }

    public Long getWonGamesCount(Long userId) {
        return repository.getWonGamesCount(userId);
    }
}
