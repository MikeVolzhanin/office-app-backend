package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "job")
    private String job;

    @Column(name = "photo")
    private String photo;

    @Column(name= "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "office", referencedColumnName = "id")
    private Office office;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "userId")
    private List<IdeaPost> posts;

    @OneToMany(mappedBy = "userId")
    private List<Likes> likes;

    @OneToMany(mappedBy = "userId")
    private List<Dislikes> dislikes;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;
}

