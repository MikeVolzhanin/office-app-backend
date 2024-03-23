package com.example.officeappbackend.dto;

import com.example.officeappbackend.Entities.Office;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String job;
    private String photo;
    private OfficeDto office;
}
