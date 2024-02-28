package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.IdeaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaPostRepository extends JpaRepository<IdeaPost, Long> {
}
