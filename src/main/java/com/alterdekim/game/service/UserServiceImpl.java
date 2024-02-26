package com.alterdekim.game.service;

import com.alterdekim.game.dto.UserDTO;
import com.alterdekim.game.entities.Role;
import com.alterdekim.game.entities.User;
import com.alterdekim.game.repository.RoleRepository;
import com.alterdekim.game.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDTO userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPronouns("she/her");
        user.setDisplayName(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName("ROLE_ADMIN");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    private UserDTO convertEntityToDto(User user){
        UserDTO userDto = new UserDTO();
        userDto.setUsername(user.getUsername());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    public void updateProfileInfo(Long userId, String displayName, String nickname, String pronouns) {
        userRepository.updateProfileInfo(userId, displayName, nickname, pronouns);
    }

    public void setAvatar(Long userId, Long imageId) {
        userRepository.updateAvatar(userId, imageId);
    }
}

