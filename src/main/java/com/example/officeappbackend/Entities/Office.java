package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "office")
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "office")
    private List<User> users;

    @OneToMany(mappedBy = "officeId")
    private List<IdeaPost> posts;
}
