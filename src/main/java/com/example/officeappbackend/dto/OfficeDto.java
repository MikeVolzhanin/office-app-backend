package com.example.officeappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfficeDto {
    private Long id;
    private String imageUrl;
    private String address;
}
