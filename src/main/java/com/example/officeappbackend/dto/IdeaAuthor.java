package com.example.officeappbackend.dto;

import lombok.Data;

@Data
public class IdeaAuthor {
    private Long id;
    private String name;
    private String surname;
    private String job;
    private String photo;
    private OfficeDto office;
}
