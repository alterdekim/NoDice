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
@Table(name = "friend_status")
public class FriendStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long firstUserId;

    @Column(nullable = false)
    private Long secondUserId;

    @Column(nullable = false)
    private Integer status;

    public FriendStatus(Long firstUserId, Long secondUserId, Integer status) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.status = status;
    }
}
