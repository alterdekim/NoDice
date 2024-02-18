package com.alterdekim.game.service;

import com.alterdekim.game.entities.TextDataVal;
import com.alterdekim.game.repository.TextDataValRepository;
import org.springframework.stereotype.Service;

@Service
public class TextDataValServiceImpl implements TextDataValService {

    private final TextDataValRepository textDataValRepository;

    public TextDataValServiceImpl(TextDataValRepository textDataValRepository) {
        this.textDataValRepository = textDataValRepository;
    }

    @Override
    public TextDataVal findById(Long id) {
        return textDataValRepository.findById(id).orElse(null);
    }
}
