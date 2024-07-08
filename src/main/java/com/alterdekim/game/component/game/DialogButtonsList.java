package com.alterdekim.game.component.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DialogButtonsList implements ActionDialogBody {
    private final List<DialogButton> buttons;
}
