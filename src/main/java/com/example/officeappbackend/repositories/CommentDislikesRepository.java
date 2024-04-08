package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.CommentDislikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDislikesRepository extends JpaRepository<Long, CommentDislikes> {
}
