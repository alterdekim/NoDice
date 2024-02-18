package com.alterdekim.game.service;

import com.alterdekim.game.entities.MarketBanner;
import com.alterdekim.game.repository.MarketBannerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private final MarketBannerRepository repository;

    public BannerServiceImpl(MarketBannerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MarketBanner> getAllActive() {
        return repository.getAllActive();
    }
}
