package com.alterdekim.game.component.processors;

import com.alterdekim.game.component.LongPoll;
import com.alterdekim.game.component.LongPollConfig;
import com.alterdekim.game.component.result.LongPollResultSingle;
import com.alterdekim.game.component.result.LongPollResultType;
import com.alterdekim.game.dto.RoomResult;
import com.alterdekim.game.dto.RoomResultState;
import com.alterdekim.game.dto.RoomResultV2;
import com.alterdekim.game.dto.UserResult;
import com.alterdekim.game.entities.Room;
import com.alterdekim.game.service.RoomPlayerService;
import com.alterdekim.game.service.RoomPlayerServiceImpl;
import com.alterdekim.game.service.RoomServiceImpl;
import com.alterdekim.game.service.UserServiceImpl;
import com.alterdekim.game.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoomProcessor extends Processor<RoomResultV2> {

    public RoomProcessor(LongPoll longPoll) {
        super(LongPollResultType.RoomResult, longPoll);
    }

    @Override
    public LongPollResultSingle<RoomResultV2> process(LongPollConfig config, Long userId) {
        List<RoomResult> clientRooms = config.getRooms();

        if( clientRooms.isEmpty() ) return new LongPollResultSingle<>(getType(), roomToRoomResult2AddChange(getParent().getRoomService().getAllActive()));

        List<RoomResult> rooms = roomToRoomResult(getParent().getRoomService().getAllActive());

        if( ListUtil.isEqualListRoom(rooms, clientRooms) ) return new LongPollResultSingle<>(getType(), new ArrayList<>());

        List<RoomResult> rr = new ArrayList<>(rooms);
        List<RoomResultV2> resultV2List = new ArrayList<>();

        for( RoomResult r : clientRooms ) {
            if( rooms.stream().noneMatch(t -> t.getId().longValue() == r.getId().longValue()) ) {
                resultV2List.add(new RoomResultV2(RoomResultState.REMOVE, r.getId(), r.getPlayerCount(), r.getPlayers()));
                continue;
            }

            RoomResult r1 = rooms.stream().filter( t -> t.getId().longValue() == r.getId().longValue()).findFirst().get();
            if( !r.equals(r1) ) {
                resultV2List.add(new RoomResultV2(RoomResultState.ADD_CHANGE, r1.getId(), r1.getPlayerCount(), r1.getPlayers()));
            }
            rr.remove(r1);
        }

        rr.forEach(r -> resultV2List.add(new RoomResultV2(RoomResultState.ADD_CHANGE, r.getId(), r.getPlayerCount(), r.getPlayers())));

        return new LongPollResultSingle<>(getType(), resultV2List.stream().sorted(Comparator.comparingLong(RoomResultV2::getId)).collect(Collectors.toList()));
    }

    private List<RoomResult> roomToRoomResult(List<Room> rooms) {
        return rooms.stream()
                .map( r -> new RoomResult(r.getId(), r.getPlayerCount(), getParent().getRoomPlayerService().findByRoomId(r.getId()).stream()
                        .map(p -> getParent().getUserService().findById(p.getUserId()))
                        .map(p -> new UserResult(p.getId(), p.getDisplayName(), p.getAvatarId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private List<RoomResultV2> roomToRoomResult2AddChange(List<Room> rooms) {
        return rooms.stream()
                .map( r -> new RoomResultV2(RoomResultState.ADD_CHANGE, r.getId(), r.getPlayerCount(), getParent().getRoomPlayerService().findByRoomId(r.getId()).stream()
                        .map(p -> getParent().getUserService().findById(p.getUserId()))
                        .map(p -> new UserResult(p.getId(), p.getDisplayName(), p.getAvatarId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
