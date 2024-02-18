package com.alterdekim.game.service;

import com.alterdekim.game.entities.MarketBanner;

import java.util.List;

public interface BannerService {
    List<MarketBanner> getAllActive();
}
