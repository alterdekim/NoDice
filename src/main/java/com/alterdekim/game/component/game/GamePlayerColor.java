package com.alterdekim.game.component.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GamePlayerColor {
    RED("#ff0000"),
    GREEN("#00ff00"),
    BLUE("#0000ff"),
    WHITE("#ffffff"),
    BLACK("#000000");

    private final String hex;

    public GamePlayerColor next() {
        return GamePlayerColor.values()[(this.ordinal() + 1) % GamePlayerColor.values().length];
    }
}
