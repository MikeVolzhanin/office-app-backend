package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.Entities.InProgress;
import com.example.officeappbackend.Entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InProgressRepository extends JpaRepository<InProgress, Long> {
    List<InProgress> findByOffice(Office office);
    Optional<InProgress> findByPostAndOffice(IdeaPost post, Office office);
}
