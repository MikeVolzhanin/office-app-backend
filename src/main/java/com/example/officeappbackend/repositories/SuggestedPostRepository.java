package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.Entities.SuggestedPosts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
public interface SuggestedPostRepository extends JpaRepository<SuggestedPosts, Long> {
    Optional<SuggestedPosts> findByPostAndOffice(IdeaPost post, Office office);
    List<SuggestedPosts> findByOffice(Office office);
    void deleteByPost(IdeaPost post);
}
