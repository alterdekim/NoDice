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
@Table(name = "mission")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long nameId;

    @Column(nullable = false)
    private Integer xp;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Boolean isActive;

    public Mission(Long nameId, Integer xp, Integer count, Long timestamp, Boolean isActive) {
        this.nameId = nameId;
        this.xp = xp;
        this.count = count;
        this.timestamp = timestamp;
        this.isActive = isActive;
    }
}
