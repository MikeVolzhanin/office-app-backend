package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "implemented")
public class Implemented {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private IdeaPost post;
    @ManyToOne
    @JoinColumn(name = "office_id", referencedColumnName = "id")
    private Office office;
}
