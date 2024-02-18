package com.alterdekim.game.service;

import com.alterdekim.game.dto.UserDTO;
import com.alterdekim.game.entities.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDto);

    User findByUsername(String usernane);

    List<UserDTO> findAllUsers();

    User findById(Long id);
}

