package com.hufsglobalion.glupshroom.domain.user.repository;

import com.hufsglobalion.glupshroom.domain.user.entity.ProfileType;
import com.hufsglobalion.glupshroom.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProfileType(ProfileType profileType);
}
