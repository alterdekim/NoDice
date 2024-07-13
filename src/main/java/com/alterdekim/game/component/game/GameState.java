package com.alterdekim.game.component.game;

import com.alterdekim.game.component.game.statemanager.MoveManager;
import com.alterdekim.game.component.game.statemanager.StateManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public enum GameState {
    MOVE(Arrays.asList(GameMsgType.DialogConfirmAnswer, GameMsgType.DialogCancelAnswer), true, MoveManager.class),
    TRADES(Collections.emptyList(), false, null),
    AUCTION(Collections.emptyList(), false, null);

    private final List<GameMsgType> allowedRequests;
    private final boolean allowedDialog;
    private final Class<? extends StateManager> managerClass;
}
