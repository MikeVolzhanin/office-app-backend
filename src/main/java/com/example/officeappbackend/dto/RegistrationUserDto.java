package com.example.officeappbackend.dto;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String confirmPassword;
}
