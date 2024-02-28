package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProfilePageResult {
    private String href;
    private String avatar;
    private String username;
    private Long id;
    private String displayName;
    private Long games;
    private Long wins;
    private Integer friends_cnt;
    private FriendFollowState follow_state;
}
