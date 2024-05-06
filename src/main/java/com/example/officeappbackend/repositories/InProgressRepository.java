package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.InProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InProgressRepository extends JpaRepository<InProgress, Long> {
}
