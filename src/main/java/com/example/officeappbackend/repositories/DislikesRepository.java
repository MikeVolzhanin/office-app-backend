package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Dislikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DislikesRepository extends JpaRepository<Dislikes, Long> {
}
