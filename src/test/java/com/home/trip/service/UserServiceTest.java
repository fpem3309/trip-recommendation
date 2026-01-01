package com.home.trip.service;

import com.home.trip.domain.User;
import com.home.trip.domain.dto.user.UserDto;
import com.home.trip.domain.dto.user.UserStatus;
import com.home.trip.domain.dto.user.UserUpdateDto;
import com.home.trip.domain.enums.Role;
import com.home.trip.exception.DuplicateUserIdException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        User findUser = userService.findByUserId(user.userId());
        Assertions.assertThat(findUser.getUserId()).isEqualTo(user.userId());
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

    @Test
    void 회원_검색() throws Exception {
        // given
        String userId = "test";

        // when
        User findUser = userService.findByUserId(userId);

        // then
        Assertions.assertThat(userId).isEqualTo(findUser.getUserId());
    }

    @Test
    void 없는_회원_검색시_예외발생() throws Exception {
        // given
        String userId = "없는 아이디";

        // when
        // then
        UsernameNotFoundException e = org.junit.jupiter.api.Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findByUserId(userId)
        );

        Assertions.assertThat(e.getMessage()).isEqualTo("일치하는 회원이 없습니다.");
    }

    @Test
    void 회원_권한_검색() throws Exception {
        // given
        String userId = "test";

        // when
        String role = userService.findRoleByUserId(userId);

        // then
        Assertions.assertThat(role).contains("ROLE_ADMIN");
        Assertions.assertThat(role).contains("ROLE_USER");
    }

    @Test
    void 없는_회원_권한_검색시_예외발생() throws Exception {
        // given
        String userId = "없는 아이디";

        // when
        // then
        UsernameNotFoundException e = org.junit.jupiter.api.Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findRoleByUserId(userId)
        );

        Assertions.assertThat(e.getMessage()).isEqualTo("일치하는 회원이 없습니다.");
    }

    @Test
    void 회원_정보_업데이트() throws Exception {
        // given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUserId("test");
        userUpdateDto.setEmail("email2"); // 바뀜
        userUpdateDto.setNickname(""); // 안바뀜

        Set<Role> roles = new HashSet<>();
        userUpdateDto.setRole(roles); // 안바뀜

        // when
        userService.updateUser(userUpdateDto);
        User findUser = userService.findByUserId(userUpdateDto.getUserId());

        // then
        Assertions.assertThat(findUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
        Assertions.assertThat(findUser.getNickname()).isNotEqualTo(userUpdateDto.getNickname());
        Assertions.assertThat(findUser.getRoles()).isNotNull();
    }

    @Test
    void 회원_탈퇴() throws Exception {
        // given
        String userId = "test2";

        // when
        userService.deleteUser(userId);

        // then
        User findUser = userService.findByUserId(userId);
        Assertions.assertThat(findUser.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
    }

    @Test
    void 이미_탈퇴된_회원_예외발생() throws Exception {
        // given
        String userId = "test";

        // when
        // then
        IllegalStateException e = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> userService.deleteUser(userId)
        );

        Assertions.assertThat(e.getMessage()).isEqualTo("이미 탈퇴 처리된 회원입니다.");
    }
}