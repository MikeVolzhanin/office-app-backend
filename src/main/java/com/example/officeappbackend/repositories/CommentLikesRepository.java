package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesRepository extends JpaRepository<Long, CommentLikes> {
}
