package com.example.officeappbackend.controllers;

import com.example.officeappbackend.dto.OfficeDto;
import com.example.officeappbackend.dto.UserDto;
import com.example.officeappbackend.service.OfficeService;
import com.example.officeappbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final OfficeService officeService;

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Principal principal){
        return userService.getUserInfo(principal.getName());
    }
    @PatchMapping
    public ResponseEntity<?> updateUserInfo(@RequestBody UserDto user){
        return userService.updateUserInfo(user);
    }
    @GetMapping("/available-offices")
    public List<OfficeDto> getAvailableOffices(){
        return officeService.getAvailableOffices().stream().map(
                officeService::convertToOfficeDto).collect(Collectors.toList());
    }
}
