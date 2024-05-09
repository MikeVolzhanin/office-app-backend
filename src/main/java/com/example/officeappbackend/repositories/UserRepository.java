package com.example.officeappbackend.repositories;

import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByOffice(Office office);
}
