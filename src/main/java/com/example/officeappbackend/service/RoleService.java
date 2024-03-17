package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.Role;
import com.example.officeappbackend.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    ///Заглушка для докера
    public void startApplication(){
        if(roleRepository.findByName("ROLE_USER").isEmpty()){
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }

        if(roleRepository.findByName("ROLE_ADMIN").isEmpty()){
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);
        }
    }

    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER").get();
    }
}
