package com.alterdekim.game.dto;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomResult {
    private Room room;
    private List<RoomPlayer> players;
}
