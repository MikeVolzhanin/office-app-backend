package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Dislikes;
import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DislikesRepository extends JpaRepository<Dislikes, Long> {
    void deleteByUserIdAndPostId(User userId, IdeaPost ideaPost);
    void deleteByPostId(IdeaPost ideaPost);
    Optional<Dislikes> findByUserIdAndPostId(User userId, IdeaPost ideaPost);
}
