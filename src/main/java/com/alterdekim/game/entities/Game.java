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
@Table(name="games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Boolean isDone;

    @Column(nullable = false)
    private Boolean isWon;

    public Game(Long userId, Boolean isDone, Boolean isWon) {
        this.userId = userId;
        this.isDone = isDone;
        this.isWon = isWon;
    }
}
