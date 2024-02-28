package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "likes")
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User UserId;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private IdeaPost PostId;

}
