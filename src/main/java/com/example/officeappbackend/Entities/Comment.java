package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private IdeaPost ideaPost;

    @Column(name = "content")
    private String content;

    @Column(name = "attached_image")
    private String attachedImage;

    @Column(name = "date")
    private Date date;

    @OneToMany(mappedBy = "comment")
    private List<CommentDislikes> commentDislikes;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "dislikes_count")
    private Integer dislikesCount;
}
