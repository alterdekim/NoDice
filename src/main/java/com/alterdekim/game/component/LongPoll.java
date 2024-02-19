package com.alterdekim.game.component;

import com.alterdekim.game.dto.LongPollResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Getter
@Slf4j
public class LongPoll {

    private final BlockingQueue<LongPollingSession> longPollingQueue = new ArrayBlockingQueue<>(100);

    @Scheduled(fixedRate = 5000)
    private void longPoll() {
        getLongPollingQueue().forEach(longPollingSession -> {
            try {
                longPollingSession.getDeferredResult().setResult(new LongPollResult());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        getLongPollingQueue().removeIf(e -> e.getDeferredResult().isSetOrExpired());
    }
}
