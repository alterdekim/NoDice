package com.alterdekim.game.component;

import com.alterdekim.game.dto.GameInvite;
import com.alterdekim.game.dto.GameRedirect;
import com.alterdekim.game.dto.RoomResult;
import com.alterdekim.game.dto.UserResult;
import com.alterdekim.game.util.Hash;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class LongPollConfig {
    private Long last_chat_id;
    private List<RoomResult> rooms;
    private Integer session_pass;
    private String poll_token;
    private List<UserResult> friends_online;
    private Long lastRequest;
    private List<GameInvite> invites;
    private List<GameRedirect> gameRedirect;

    public LongPollConfig(Long last_chat_id) {
        this.last_chat_id = last_chat_id;
        this.rooms = new ArrayList<>();
        this.session_pass = 0;
        this.poll_token = Hash.rnd();
        this.friends_online = new ArrayList<>();
        this.lastRequest = System.currentTimeMillis();
        this.invites = new ArrayList<>();
        this.gameRedirect = new ArrayList<>();
    }
}
