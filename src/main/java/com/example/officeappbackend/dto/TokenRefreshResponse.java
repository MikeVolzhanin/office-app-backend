package com.example.officeappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class TokenRefreshResponse{
    private String accessToken;
    private String refreshToken;
}
