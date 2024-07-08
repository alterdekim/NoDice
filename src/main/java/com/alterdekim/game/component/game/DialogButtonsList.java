package com.alterdekim.game.component.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DialogButtonsList extends ActionDialogBody {
    @JsonIgnore
    private final List<DialogButton> buttons;

    @Override
    public Object getVal() {
        return this.buttons;
    }
}
