package com.example.officeappbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilterDto {
    private List<Long> officesId;
    private Integer sortingFilterId;
    private String text;
}
