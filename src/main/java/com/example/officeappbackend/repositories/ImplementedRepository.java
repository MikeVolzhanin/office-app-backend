package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.Entities.Implemented;
import com.example.officeappbackend.Entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImplementedRepository extends JpaRepository<Implemented, Long>{
    List<Implemented> findByOffice(Office office);
    Optional<Implemented> findByPostAndOffice(IdeaPost post, Office office);
}
