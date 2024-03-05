package com.alterdekim.game.component.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chip {
    private Long uid;
    private Integer x;
    private Integer y;
    private String color;
}
