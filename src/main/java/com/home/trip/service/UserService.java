package com.home.trip.service;

import com.home.trip.domain.User;
import com.home.trip.domain.dto.UserDto;
import com.home.trip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void join(UserDto userDto) {
        User user = User.createUser(userDto);
        userRepository.save(user);
    }
}
