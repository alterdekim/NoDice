package com.alterdekim.game.component.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardTile {
    private String uid;
    private Integer id;
    private Integer cost;
    private String stars;
    private String img;
    private String color;
    private String ownerColor;
}
