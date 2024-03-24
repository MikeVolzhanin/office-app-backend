package com.example.officeappbackend.dto;

import lombok.Data;

@Data
public class UserInfoForm {
    private String name;
    private String surname;
    private String job;
    private String photo;
    private Long office;
}
