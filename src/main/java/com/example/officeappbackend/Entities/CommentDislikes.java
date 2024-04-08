package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comment_dislikes")
public class CommentDislikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "comment", referencedColumnName = "id")
    private Comment comment;
}
