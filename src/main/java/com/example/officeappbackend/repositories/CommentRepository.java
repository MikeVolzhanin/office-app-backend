package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Comment;
import com.example.officeappbackend.Entities.IdeaPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIdeaPost(IdeaPost ideaPost);
}
