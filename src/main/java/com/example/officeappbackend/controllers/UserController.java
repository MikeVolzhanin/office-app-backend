package com.example.officeappbackend.controllers;

import com.example.officeappbackend.dto.IdeaAuthor;
import com.example.officeappbackend.dto.OfficeDto;
import com.example.officeappbackend.dto.UserDto;
import com.example.officeappbackend.dto.UserInfoForm;
import com.example.officeappbackend.service.OfficeService;
import com.example.officeappbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final OfficeService officeService;
    @PatchMapping
    public ResponseEntity<?> updateUserInfo(@RequestBody UserInfoForm user, Principal principal){
        return userService.updateUserInfo(user, principal);
    }
    @GetMapping("/offices")
    public List<OfficeDto> getAvailableOffices(){
        return officeService.getAvailableOffices().stream().map(officeService::convertToOfficeDto).collect(Collectors.toList());
    }
    @GetMapping("/authors/{id}")
    public ResponseEntity<?> getAuthor(@PathVariable("id") Long id){
        IdeaAuthor author = userService.findByAuthorId(id);
        if(author == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(author);
    }

    @GetMapping("/my-office/employees")
    public ResponseEntity<?> getEmployees(@RequestParam(name="page") Integer page, @RequestParam(name="page_size") Integer pageSize){
        return null;
    }

}
