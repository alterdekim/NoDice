package com.alterdekim.game.component.game.statemanager;

import com.alterdekim.game.component.game.GameRoom;
import com.alterdekim.game.websocket.message.BasicMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class StateManager {

    private final GameRoom parent;

    public abstract void performState();
    public abstract void performDialogAction(BasicMessage message);
}
