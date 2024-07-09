package com.alterdekim.game.websocket.message;

public enum WebSocketMessageType {
    PlayersList,
    BoardGUI,
    InfoRequest,
    ChangeBoardTileState,
    AssignChip,
    ChipMove,
    PlayerColor,
    ShowDialog,
    HideDialog,
    ShowFieldInfo,
    PerformDialogActions
}
