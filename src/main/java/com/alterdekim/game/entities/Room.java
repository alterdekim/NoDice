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
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer playerCount;

    @Column(nullable = false)
    private Boolean isPrivate;

    @Column(nullable = false)
    private String password;

    public Room(Integer playerCount, Boolean isPrivate, String password) {
        this.playerCount = playerCount;
        this.isPrivate = isPrivate;
        this.password = password;
    }
}
