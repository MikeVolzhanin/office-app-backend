package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "in_progress")
public class InProgress {
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
