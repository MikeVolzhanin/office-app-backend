package com.example.officeappbackend.controllers;

import com.example.officeappbackend.Entities.RefreshToken;
import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.exceptions.TokenRefreshException;
import com.example.officeappbackend.service.AuthService;
import com.example.officeappbackend.service.RefreshTokenService;
import com.example.officeappbackend.service.UserService;
import com.example.officeappbackend.util.JwtTokenUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        return authService.createAuthToken(authRequest);
    }
    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto){
        return authService.createNewUser(registrationUserDto);
    }
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Principal principal){
        return userService.getUserInfo(principal.getName());
    }
    @GetMapping("/email-valid")
    public ResponseEntity<?> emailValidation(@RequestParam(name = "email") String email){
        return authService.emailValidation(email);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody String request) {
        return refreshTokenService.findByToken(request)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtTokenUtils.generateToken(
                            userService.loadUserByUsername(user.getEmail())
                    );
                    return ResponseEntity.ok(new TokenRefreshResponse(token, refreshTokenService.createRefreshToken(user.getId()).getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(request,
                        "Refresh token is not in database!"));
    }
}

