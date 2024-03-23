package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.RefreshToken;
import com.example.officeappbackend.Entities.User;
import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.exceptions.AppError;
import com.example.officeappbackend.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()
            ));
        } catch(BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль", new Date()), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authRequest.getEmail());
        User user =  userService.findByEmail(userDetails.getUsername()).get();
        String token = jwtTokenUtils.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                userService.findByEmail(userDetails.getUsername()).get().getId()
        );

        return ResponseEntity.ok(new TokenRefreshResponse(token, refreshToken.getToken()));
    }

    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto){
        if(!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают", new Date()), HttpStatus.BAD_REQUEST);
        }
        if(userService.findByEmail(registrationUserDto.getEmail()).isPresent()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователи с указанным email уже существуют", new Date()), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);
        return createAuthToken(new JwtRequest(registrationUserDto.getEmail(), registrationUserDto.getPassword()));
    }

    public ResponseEntity<?> emailValidation(String email){
        if(userService.findByEmail(email).isPresent()){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
