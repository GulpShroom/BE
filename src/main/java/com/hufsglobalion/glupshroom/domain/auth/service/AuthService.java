package com.hufsglobalion.glupshroom.domain.auth.service;

import com.hufsglobalion.glupshroom.domain.auth.dto.response.ProfileSelectResponse;
import com.hufsglobalion.glupshroom.domain.user.entity.ProfileType;
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
public class AuthService {

    private final UserRepository userRepository;

    public ProfileSelectResponse selectProfile(String profileTypeValue) {
        ProfileType profileType = ProfileType.from(profileTypeValue);
        User user = userRepository.findFirstByProfileTypeOrderByIdDesc(profileType)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_SELECTION_FAILED));
        return ProfileSelectResponse.from(user);
    }
}
