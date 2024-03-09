package com.example.officeappbackend.dto;

import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.Entities.SortingFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class Filters {
    List<OfficeDto> offices;
    List<SortingFilter> sortingFilters;
}
