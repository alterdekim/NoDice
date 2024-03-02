package com.alterdekim.game.component.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardGUI {
    private List<BoardTile> top;
    private List<BoardTile> right;
    private List<BoardTile> bottom;
    private List<BoardTile> left;
    private List<CornerTile> corners;
}
