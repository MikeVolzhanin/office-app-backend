package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.User;
import com.example.officeappbackend.configs.PasswordEncoder;
import com.example.officeappbackend.dto.*;
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

import java.security.Principal;
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

        String defaultPhoto = "https://previews.123rf.com/images/verpeya/verpeya1902/verpeya190202090/129973840-user-icon.jpg";
        user.setSurname(registrationUserDto.getUserInfo().getSurname());
        user.setName(registrationUserDto.getUserInfo().getName());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.passwordEncoder().encode(registrationUserDto.getPassword()));

        user.setJob(registrationUserDto.getUserInfo().getJob());
        user.setCreatedAt(new Date());
        if(registrationUserDto.getUserInfo().getPhoto() == null)
            user.setPhoto(defaultPhoto);
        else
            user.setPhoto(registrationUserDto.getUserInfo().getPhoto());

        user.setOffice(officeService.findById(registrationUserDto.getUserInfo().getOffice()).orElse(null));
        user.setRoles(List.of(roleService.getUserRole()));

        return userRepository.save(user);
    }

    public ResponseEntity<?> getUserInfo(String email){
        User user = findByEmail(email).orElseThrow();
        return ResponseEntity.ok(convertToUserDto(user));
    }

    @Transactional
    public ResponseEntity<?> updateUserInfo(UserInfoForm user, Principal principal){
        User updatedUser = findByEmail(principal.getName()).get();

        updatedUser.setOffice(officeService.findById(user.getOffice()).get());
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
    public IdeaAuthor findByAuthorId(Long id){
        User author = findById(id).orElse(null);
        if(author == null)
            return null;
        return convertToIdeaAuthor(author);
    }

    public IdeaAuthor convertToIdeaAuthor(User user){
        IdeaAuthor ideaAuthor = new IdeaAuthor();
        ideaAuthor.setId(user.getId());
        ideaAuthor.setName(user.getName());
        ideaAuthor.setSurname(user.getSurname());
        ideaAuthor.setPhoto(user.getPhoto());
        ideaAuthor.setOffice(officeService.convertToOfficeDto(user.getOffice()));
        ideaAuthor.setJob(user.getJob());
        ideaAuthor.setJob(user.getJob());
        return ideaAuthor;
    }
    public UserDto convertToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getJob(),
                user.getPhoto(),
                officeService.convertToOfficeDto(user.getOffice())
        );
    }
}
