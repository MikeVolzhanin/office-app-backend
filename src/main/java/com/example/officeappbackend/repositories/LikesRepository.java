package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.Entities.Likes;
import com.example.officeappbackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    void deleteByUserIdAndPostId(User userId, IdeaPost post);
    Optional<Likes> findByUserIdAndPostId(User userId, IdeaPost post);
}
