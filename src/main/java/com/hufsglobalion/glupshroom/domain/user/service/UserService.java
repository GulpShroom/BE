package com.hufsglobalion.glupshroom.domain.user.service;

import com.hufsglobalion.glupshroom.domain.user.entity.User;
import com.hufsglobalion.glupshroom.domain.user.repository.UserRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
