package com.alterdekim.game.component;

import com.alterdekim.game.dto.LongPollResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.context.request.async.DeferredResult;

@AllArgsConstructor
@Getter
public class LongPollingSession {
    private Long userId;
    private DeferredResult<LongPollResult> deferredResult;
}
