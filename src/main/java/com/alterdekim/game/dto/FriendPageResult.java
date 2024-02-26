package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendPageResult {
    private String href;
    private String avatar;
    private String username;
    private Long id;
    private String displayName;
}
