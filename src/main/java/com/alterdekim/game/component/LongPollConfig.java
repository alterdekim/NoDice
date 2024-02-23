package com.alterdekim.game.component;

import com.alterdekim.game.dto.GameInvite;
import com.alterdekim.game.dto.RoomResult;
import com.alterdekim.game.dto.UserResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
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
}
