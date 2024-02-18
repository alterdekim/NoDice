package com.alterdekim.game.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 955)
    @NotNull
    private String message;

    @Column(nullable = false)
    private Long createdAt;

    public Chat(Long userId, String message, Long createdAt) {
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }
}
