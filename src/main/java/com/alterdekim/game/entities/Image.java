package com.alterdekim.game.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Image {

    @Id
    @GeneratedValue
    Long id;

    @Lob
    @Column(length=16777211)
    byte[] content;
}
