package com.example.officeappbackend.dto;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private String email;
    private String password;
    private UserInfoForm userInfo;
}
