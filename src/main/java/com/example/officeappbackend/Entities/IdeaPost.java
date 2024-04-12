package com.example.officeappbackend.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "idea_post")
public class IdeaPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "office_id", referencedColumnName = "id")
    private Office officeId;

    @Column(name = "attached_images")
    private String attachedImages;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "dislikes_count")
    private Integer dislikesCount;

    @Column(name = "comments_count")
    private Integer commentsCount;

    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "postId")
    private List<Likes> likes;

    @OneToMany(mappedBy = "postId")
    private List<Dislikes> dislikes;

    @OneToMany(mappedBy = "ideaPost")
    private List<Comment> comments;
}
