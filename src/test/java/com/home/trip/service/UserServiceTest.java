package com.home.trip.service;

import com.home.trip.domain.User;
import com.home.trip.domain.dto.UserDto;
import com.home.trip.domain.enums.Role;
import com.home.trip.exception.DuplicateUserIdException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @PersistenceContext
    EntityManager em;

    @Test
    void 회원가입() throws Exception {
        // given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        UserDto user = new UserDto("member1", "123", "member1@member.com",
                "member", roles, null, LocalDateTime.now(), LocalDateTime.now());

        // when
        userService.join(user);

        // then
        User findUser = userService.findByUserId(user.getUserId());
        Assertions.assertThat(findUser.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    void 회원가입_아이디_중복시_예외발생_비즈니스레벨() throws Exception {
        // given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        UserDto user1 = new UserDto("sameId", "123", "sameId1@member.com",
                "sameId1", roles, null, LocalDateTime.now(), LocalDateTime.now());

        UserDto user2 = new UserDto("sameId", "456", "sameId2@member.com",
                "sameId2", roles, null, LocalDateTime.now(), LocalDateTime.now());

        // when
        userService.join(user1);

        // then
        DuplicateUserIdException e = org.junit.jupiter.api.Assertions.assertThrows(DuplicateUserIdException.class,
                () -> userService.join(user2));

        Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 아이디입니다!");

    }

    @Test
    void 회원가입_DB에_아이디_중복시_예외발생_DB레벨() throws Exception {
        // given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        UserDto user1 = new UserDto("sameId", "123", "sameId1@member.com",
                "sameId1", roles, null, LocalDateTime.now(), LocalDateTime.now());

        UserDto user2 = new UserDto("sameId", "456", "sameId2@member.com",
                "sameId2", roles, null, LocalDateTime.now(), LocalDateTime.now());

        // when
        userService.join(user1);
        em.flush();

        // then
        org.junit.jupiter.api.Assertions.assertThrows(DuplicateUserIdException.class, () -> {
            userService.join(user2);
            em.flush();
        });
    }
}