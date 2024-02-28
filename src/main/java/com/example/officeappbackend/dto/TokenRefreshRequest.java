package com.example.officeappbackend.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
