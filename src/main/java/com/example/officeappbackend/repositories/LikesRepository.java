package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
}
