package com.example.officeappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EditPostDto {
    private String title;
    private String content;
    private List<String> attachedImages;
}
