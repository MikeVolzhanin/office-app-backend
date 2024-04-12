package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
