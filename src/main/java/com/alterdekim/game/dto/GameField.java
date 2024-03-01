package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameField {
    private Long uid;
    private Integer id;
    private String cost;
    private String img;
    private String stars;
}
