package com.alterdekim.game.component.processors;

import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.component.LongPollConfig;
import com.alterdekim.game.component.result.LongPollResultSingle;
import com.alterdekim.game.component.result.LongPollResultType;
import com.alterdekim.game.dto.FriendResult;
import com.alterdekim.game.dto.FriendState;
import com.alterdekim.game.dto.UserResult;
import com.alterdekim.game.service.FriendServiceImpl;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.ListUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FriendProcessor extends Processor<FriendResult> {

    public FriendProcessor(LongPoll parent) {
        super(LongPollResultType.FriendResult, parent);
    }

    @Override
    public LongPollResultSingle<FriendResult> process(LongPollConfig config, Long userId) {
        List<UserResult> clientFriends = config.getFriends_online();

        if( !clientFriends.isEmpty() ) return new LongPollResultSingle<>(getType(), rearrangeFriends(clientFriends, userId));

        return new LongPollResultSingle<>(getType(), friends2Users(getParent().getFriendService().getFriendsOfUserId(userId))
                .stream()
                .map(f -> new FriendResult(FriendState.ADD, f.getId(), f.getUsername()))
                .collect(Collectors.toList()));
    }

    private List<FriendResult> rearrangeFriends(List<UserResult> clientFriends, Long userId) {
        List<UserResult> userResults = friends2Users(getParent().getFriendService().getFriendsOfUserId(userId));

        if(ListUtil.isEqualListUser(userResults, clientFriends) ) return new ArrayList<>();

        List<UserResult> urr = new ArrayList<>(userResults);
        List<FriendResult> fr = new ArrayList<>();
        for( UserResult r : clientFriends ) {
            if( userResults.stream().noneMatch(t -> t.getId().longValue() == r.getId().longValue()) ) {
                fr.add(new FriendResult(FriendState.REMOVE, r.getId(), r.getUsername()));
                continue;
            }
            UserResult r1 = userResults.stream().filter( t -> t.getId().longValue() == r.getId().longValue()).findFirst().get();
            if( !r.equals(r1) ) {
                fr.add(new FriendResult(FriendState.ADD, r1.getId(), r1.getUsername()));
            }
            urr.remove(r1);
        }
        urr.forEach(r -> fr.add(new FriendResult(FriendState.ADD, r.getId(), r.getUsername())));
        return fr.stream().sorted(Comparator.comparingLong(FriendResult::getId)).collect(Collectors.toList());
    }

    private List<UserResult> friends2Users(List<Long> friendIds) {
        return friendIds.stream()
                .map(id -> getParent().getUserService().findById(id))
                .map(u -> new UserResult(u.getId(), u.getUsername()))
                .filter(f -> getParent().getMap().keySet().stream().anyMatch(l -> l.longValue() == f.getId().longValue()))
                .collect(Collectors.toList());
    }
}
