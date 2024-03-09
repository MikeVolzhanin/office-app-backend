package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.User;
import com.example.officeappbackend.configs.PasswordEncoder;
import com.example.officeappbackend.dto.RegistrationUserDto;
import com.example.officeappbackend.dto.Success;
import com.example.officeappbackend.dto.UserDto;
import com.example.officeappbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final OfficeService officeService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    public Optional<User> findByEmail(String username){
        return userRepository.findByEmail(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользлватель '%s' не найден", username)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto){
        User user = new User();
        user.setSurname(registrationUserDto.getSurname());
        user.setName(registrationUserDto.getName());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.passwordEncoder().encode(registrationUserDto.getPassword()));

        user.setJob(null);
        user.setCreatedAt(new Date());
        // Нужно задавать из вне информация про офис
        user.setOffice(officeService.findById(1L).orElse(null));
        user.setRoles(List.of(roleService.getUserRole()));

        return userRepository.save(user);
    }

    public ResponseEntity<?> getUserInfo(String email){
        User user = findByEmail(email).orElseThrow();
        return ResponseEntity.ok(convertToUserDto(user));
    }

    @Transactional
    public ResponseEntity<?> updateUserInfo(UserDto user){
        User updatedUser = findByEmail(user.getEmail()).orElse(null);

        updatedUser.setOffice(officeService.findByAddress(user.getOffice().get(0)).orElse(null));
        updatedUser.setJob(user.getJob());
        updatedUser.setName(user.getName());
        updatedUser.setSurname(user.getSurname());
        updatedUser.setPhoto(user.getPhoto());

        userRepository.save(updatedUser);

        return  ResponseEntity.ok(
                new Success("user was updated successfully", new Date())
        );
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public UserDto convertToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getJob(),
                user.getPhoto(),
                List.of(
                        user.getOffice().getAddress(),
                        user.getOffice().getImageUrl()
                )
        );
    }
}
