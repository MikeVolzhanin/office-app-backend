package com.example.officeappbackend.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public class SortingFilter {
    private Integer id;
    private String name;
}
