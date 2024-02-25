package com.alterdekim.game.component.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class LongPollResultSingle<T> {
    private LongPollResultType type;
    private List<T> array;
}
