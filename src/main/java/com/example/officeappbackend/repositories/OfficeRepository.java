package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Office;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    Optional<Office> findByAddress(String address);
}
