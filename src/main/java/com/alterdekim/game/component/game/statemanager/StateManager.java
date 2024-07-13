package com.alterdekim.game.component.game.statemanager;

import com.alterdekim.game.component.game.GameRoom;
import com.alterdekim.game.websocket.message.BasicMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class StateManager {

    private GameRoom parent;

    public abstract void performState();
    public abstract void performDialogAction(BasicMessage message);
}
