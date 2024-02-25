package com.alterdekim.game.dto;

import com.alterdekim.game.entities.Room;
import com.alterdekim.game.entities.RoomPlayer;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.util.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomResult {
    private Long id;
    private Integer playerCount;
    private List<UserResult> players;

    public boolean equals(RoomResult r2) {
        return this.getId().longValue() == r2.getId().longValue() &&
                this.getPlayerCount().intValue() == r2.getPlayerCount().intValue() &&
                ListUtil.isEqualListUser(this.getPlayers(), r2.getPlayers());
    }
}
