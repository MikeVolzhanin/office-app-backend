package com.example.officeappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Success {
    private String message;
    private Date timestamp;
}
