package com.alterdekim.game.util;

import com.alterdekim.game.dto.RoomResult;
import com.alterdekim.game.dto.UserResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtil {
    public static Boolean isEqualListRoom(List<RoomResult> r1, List<RoomResult> r2) {
        r1 = r1.stream().sorted(Comparator.comparingLong(RoomResult::getId)).collect(Collectors.toList());
        r2 = r2.stream().sorted(Comparator.comparingLong(RoomResult::getId)).collect(Collectors.toList());
        if( r1.size() != r2.size() ) return false;
        for( int i = 0; i < r1.size(); i++ ) {
            if( !r1.get(i).equals(r2.get(i)) ) return false;
        }
        return true;
    }

    public static Boolean isEqualListUser(List<UserResult> r1, List<UserResult> r2) {
        r1 = r1.stream().sorted(Comparator.comparingLong(UserResult::getId)).collect(Collectors.toList());
        r2 = r2.stream().sorted(Comparator.comparingLong(UserResult::getId)).collect(Collectors.toList());
        if( r1.size() != r2.size() ) return false;
        for( int i = 0; i < r1.size(); i++ ) {
            if( r1.get(i).getId().longValue() != r2.get(i).getId().longValue() ||
                    !r1.get(i).getUsername().equals(r2.get(i).getUsername()) ) {
                return false;
            }
        }
        return true;
    }
}
