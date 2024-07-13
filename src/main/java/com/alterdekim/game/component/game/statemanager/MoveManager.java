package com.alterdekim.game.component.game.statemanager;

import com.alterdekim.game.component.game.*;
import com.alterdekim.game.websocket.message.BasicMessage;
import com.alterdekim.game.websocket.message.WebSocketMessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveManager extends StateManager {
    public MoveManager(GameRoom parent) {
        super(parent);
    }

    @Override
    public void performState() {
        List<DialogButton> buttons = new ArrayList<>();
        buttons.add(new DialogButton("Button1", DialogButtonColor.GREEN, Collections.singletonList(GameMsgType.DialogConfirmAnswer)));
        DialogButtonsList b = new DialogButtonsList(buttons);
        this.getParent().getPlayers().forEach(p -> this.getParent().sendMessage(p.getUserId(), WebSocketMessageType.ShowDialog, new ActionDialog("Title yup!", "Description yay!", ActionDialogType.Buttons, b)));
        this.getParent().setGameLoopFrozen(true);
    }

    @Override
    public void performDialogAction(BasicMessage message) {

    }
}
