package com.alterdekim.game.component.processors;

import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.component.LongPollConfig;
import com.alterdekim.game.component.result.LongPollResultSingle;
import com.alterdekim.game.component.result.LongPollResultType;
import com.alterdekim.game.dto.ChatResult;
import com.alterdekim.game.dto.UserResult;
import com.alterdekim.game.entities.Chat;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.service.ChatServiceImpl;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatProcessor extends Processor<ChatResult> {

    public ChatProcessor(LongPoll longPoll) {
        super(LongPollResultType.ChatResult, longPoll);
    }

    @Override
    public LongPollResultSingle<ChatResult> process(LongPollConfig config, Long userId) {
        List<Chat> results = getParent().getChatService().getAfterLastChatId(config.getLast_chat_id());

        if( results.isEmpty() ) return new LongPollResultSingle<>(getType(), new ArrayList<>());

        return new LongPollResultSingle<>(getType(), results.stream()
                .peek(c -> c.setMessage(StringUtil.escapeTags(getParent().getUserService(), c.getMessage())))
                .map(c -> {
                    User u1 = getParent().getUserService().findById(c.getUserId());
                    return new ChatResult(c, new UserResult( c.getUserId(), u1.getUsername(), u1.getAvatarId()));
                }).collect(Collectors.toList()));
    }
}
