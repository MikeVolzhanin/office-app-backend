package com.example.officeappbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class PublishPostDto {
    private String title;
    private String content;
    private Long author;
    private Long office;
    private List<String> attachedImages;
}
