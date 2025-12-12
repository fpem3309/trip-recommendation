package com.home.trip.service;

import com.home.trip.domain.User;
import com.home.trip.domain.dto.UserDto;
import com.home.trip.domain.enums.Role;
import com.home.trip.exception.DuplicateUserIdException;
import com.home.trip.repository.UserRepository;
import com.home.trip.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserUtil userUtil;

    @Transactional
    public void join(UserDto userDto) {
        if (userRepository.existsByUserId(userDto.getUserId())) {
            throw new DuplicateUserIdException();
        }
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.createUser(userDto, encodedPassword);
        userRepository.save(user);
    }

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));
    }

    public String findRoleByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));

        return userUtil.userRoleToStringWithComma(user.getRoles(), Role::name);
    }
}
