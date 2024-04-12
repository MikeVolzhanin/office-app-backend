package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Comment;
import com.example.officeappbackend.Entities.CommentLikes;
import com.example.officeappbackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    Optional<CommentLikes> findByAuthorAndComment(User author, Comment comment);
    void deleteByAuthorAndComment(User author, Comment comment);
}
