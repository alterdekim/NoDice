package com.alterdekim.game.service;

import com.alterdekim.game.entities.Invite;
import com.alterdekim.game.repository.InviteRepository;
import org.springframework.stereotype.Service;

@Service
public class InviteServiceImpl implements InviteService {

    private final InviteRepository repository;

    public InviteServiceImpl(InviteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Invite findById(Integer id) {
        return repository.findById(id).orElse(null);
    }
}
