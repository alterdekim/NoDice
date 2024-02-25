package com.alterdekim.game.component.processors;

import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.component.LongPollConfig;
import com.alterdekim.game.component.result.LongPollResultSingle;
import com.alterdekim.game.component.result.LongPollResultType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class Processor<T> {
    private LongPollResultType type;
    private LongPoll parent;

    public abstract LongPollResultSingle<T> process(LongPollConfig config, Long userId);
}
