package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.RefreshToken;
import com.example.officeappbackend.Entities.User;
import com.example.officeappbackend.exceptions.TokenRefreshException;
import com.example.officeappbackend.repositories.RefreshTokenRepository;
import com.example.officeappbackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${refresh-token.duration}")
    private Duration refreshTokenDurationMin;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        User user = userRepository.findById(userId).get();

        if(findByUserId(user.getId()).isPresent()){
            deleteByUserId(user.getId());
        }

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + refreshTokenDurationMin.toMillis());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiredDate);
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(new Date()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new sign in request");
        }
        return token;
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    public Optional<RefreshToken> findByUserId(Long id){
        return refreshTokenRepository.findByUserId(id);
    }
}
