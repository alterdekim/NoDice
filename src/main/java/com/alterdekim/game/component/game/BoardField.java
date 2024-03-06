package com.alterdekim.game.component.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardField {
    private String uid;
    private Integer x;
    private Integer y;
    private Integer cost;
    private Integer stars;
    private String img;
    private String color;

    public BoardTile toBoardTile() {
        return new BoardTile(this.uid, 0, this.cost, "★".repeat(stars), this.img, this.color, "");
    }
}
