package com.alterdekim.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResult {
    private Long id;
    private String username;

    public boolean equals(UserResult r2) {
        return this.getId().longValue() == r2.getId().longValue() &&
                this.getUsername().equals(r2.getUsername());
    }
}
