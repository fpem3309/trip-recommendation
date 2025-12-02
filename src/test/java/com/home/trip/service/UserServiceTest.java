package com.home.trip.service;

import com.home.trip.domain.User;
import com.home.trip.domain.dto.UserDto;
import com.home.trip.domain.enums.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;


    @Test
    void 회원가입() throws Exception {
        // given
        UserDto userDto = new UserDto();
        userDto.setUserId("member1");
        userDto.setPassword("123");
        userDto.setEmail("member1@member.com");
        userDto.setNickname("member");
        userDto.setRole(Role.ROLE_USER);
        userDto.setOauthProvider(null);

        // when
        userService.join(userDto);

        // then
        User findUser = userService.findByUserId(userDto.getUserId());
        Assertions.assertThat(findUser.getUserId()).isEqualTo(userDto.getUserId());
    }
}