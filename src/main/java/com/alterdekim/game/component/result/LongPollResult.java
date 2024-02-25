package com.alterdekim.game.component.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LongPollResult {
    private List<LongPollResultSingle> result;

    public <T> List<T> getResultWithType(LongPollResultType type, T elementClass) {
        return (List<T>) result.stream().filter(r -> r.getType() == type).findFirst().get().getArray();
    }
}
