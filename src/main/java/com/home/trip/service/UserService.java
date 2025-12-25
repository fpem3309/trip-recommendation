package com.home.trip.service;

import com.home.trip.domain.User;
import com.home.trip.domain.dto.user.UserDto;
import com.home.trip.domain.dto.user.UserUpdateDto;
import com.home.trip.domain.enums.Role;
import com.home.trip.exception.DuplicateUserIdException;
import com.home.trip.repository.UserRepository;
import com.home.trip.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
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

    public Page<UserDto> findAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(user -> new UserDto(user.getUserId(),
                user.getPassword(), user.getEmail(), user.getNickname(), user.getRoles(),
                user.getOauth_provider(), user.getCreatedAt(), user.getUpdatedAt()));
    }

    /**
     * 회원 정보 업데이트
     *
     * @param userUpdateDto 클라이언트에서 폼으로 요청(userId, email, nickname, role)
     * @throws IllegalArgumentException 일치하는 userId가 없을 때
     */
    @Transactional
    public void updateUser(UserUpdateDto userUpdateDto) {
        User user = findByUserId(userUpdateDto.getUserId());
        user.changeUserInfo(userUpdateDto);
    }

    /**
     * 회원 삭제(상태값, 삭제 날짜 업데이트)
     *
     * @param userId 회원 아이디
     * @throws IllegalArgumentException 일치하는 userId가 없을 때
     * @throws IllegalStateException 이미 탈퇴 처리된 회원일 때
     */
    @Transactional
    public void deleteUser(String userId) {
        User user = findByUserId(userId);
        if (user.isWithdrawn()) {
            throw new IllegalStateException("이미 탈퇴 처리된 회원입니다.");
        }
        user.withdraw();
    }
}
