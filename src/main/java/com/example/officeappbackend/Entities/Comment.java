package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "comment")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    private User author;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Date date;
}
