package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentDislikesRepository extends JpaRepository<CommentDislikes, Long> {
    Optional<CommentDislikes> findByAuthorAndComment(User author, Comment comment);
    void deleteByAuthorAndComment(User author, Comment comment);
}
