package com.example.officeappbackend.repositories;


import com.example.officeappbackend.Entities.RefreshToken;
import com.example.officeappbackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long UserId);
    int deleteByUser(User user);
}
