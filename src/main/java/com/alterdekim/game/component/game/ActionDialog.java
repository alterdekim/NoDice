package com.alterdekim.game.component.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ActionDialog<T> {
    private String dialogTitle;
    private String dialogDescription;
    private ActionDialogType actionDialogType;
    private T actionDialogBody;
}
