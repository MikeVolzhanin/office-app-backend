package com.example.officeappbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilterDto {
    private List<Integer> officesId;
    private Integer sortingFilterId;
}
