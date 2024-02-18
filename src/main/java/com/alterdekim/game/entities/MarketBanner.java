package com.alterdekim.game.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market_banner")
public class MarketBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Long titleId;

    @Column(nullable = false)
    private Long descriptionId;

    @Column(nullable = false)
    private String gradientInfo;

    @Column(nullable = false)
    private String imageUrl;

    public MarketBanner(Boolean isActive, Long titleId, Long descriptionId, String gradientInfo, String imageUrl) {
        this.isActive = isActive;
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.gradientInfo = gradientInfo;
        this.imageUrl = imageUrl;
    }
}
