package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaPostRepository extends JpaRepository<IdeaPost, Long> {
    List<IdeaPost> findByOfficeId(Office officeId);
    List<IdeaPost> findByUserId(User user);
}
