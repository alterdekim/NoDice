package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomResultV2 {
    private RoomResultState action;
    private Long id;
    private Integer playerCount;
    private List<UserResult> players;
}
