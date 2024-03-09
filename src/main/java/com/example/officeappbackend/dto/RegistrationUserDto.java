package com.example.officeappbackend.dto;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String surname;
}
