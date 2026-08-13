package com.hufsglobalion.glupshroom.domain.user;

import com.hufsglobalion.glupshroom.domain.user.entity.ProfileType;
import com.hufsglobalion.glupshroom.domain.user.entity.User;
import com.hufsglobalion.glupshroom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        createIfAbsent(ProfileType.FIRST_KEEPER, "민지");
        createIfAbsent(ProfileType.NEXT_KEEPER, "서준");
    }

    private void createIfAbsent(ProfileType profileType, String nickname) {
        userRepository.findByProfileType(profileType).orElseGet(() ->
                userRepository.save(User.builder()
                        .profileType(profileType)
                        .nickname(nickname)
                        .build()));
    }
}
