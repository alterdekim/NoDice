package com.alterdekim.game.dto;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomResult {
    private Long id;
    private Integer playerCount;
    private List<UserResult> players;
}
