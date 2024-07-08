package com.alterdekim.game.component.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ActionDialogBody {
    @JsonProperty("value")
    public abstract Object getVal();
}
