package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.Role;
import com.example.officeappbackend.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER").get();
    }
}
