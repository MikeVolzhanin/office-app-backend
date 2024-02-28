package com.example.officeappbackend.controllers;

import com.example.officeappbackend.dto.PublishPostDto;
import com.example.officeappbackend.service.IdeaPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class IdeaPostController {
    private final IdeaPostService ideaPostService;
    @PostMapping("/publish")
    public ResponseEntity<?> publishPost(@RequestBody PublishPostDto post){
        ideaPostService.publishPost(post);
        return ResponseEntity.ok("Пост добавлен");
    }
}
