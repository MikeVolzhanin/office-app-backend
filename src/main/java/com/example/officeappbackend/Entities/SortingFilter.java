package com.example.officeappbackend.Entities;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sorting_filter")
public class SortingFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
}
