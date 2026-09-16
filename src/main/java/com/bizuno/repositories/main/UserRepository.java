package com.bizuno.repositories.main;

import com.bizuno.models.main.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.email = :phoneOrEmail OR u.phoneNumber = :phoneOrEmail")
    Optional<User> findUserByCredential(@Param("phoneOrEmail") String phoneOrEmail);
}
