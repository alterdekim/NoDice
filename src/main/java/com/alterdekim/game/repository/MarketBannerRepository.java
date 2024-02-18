package com.alterdekim.game.repository;

import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.MarketBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarketBannerRepository extends JpaRepository<MarketBanner, Long>  {

    @Query(value = "SELECT m FROM MarketBanner m WHERE m.isActive = true")
    List<MarketBanner> getAllActive();
}
