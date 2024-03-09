package com.example.officeappbackend.controllers;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.service.IdeaPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class IdeaPostController {
    private final IdeaPostService ideaPostService;

    @PostMapping("/publish")
    public ResponseEntity<?> publishPost(@RequestBody PublishPostDto post, Principal principal){
        ideaPostService.publishPost(post, principal);
        return ResponseEntity.ok(new Success(
                "Post added successfully",
                new Date()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable("id") Long id, @RequestBody EditPostDto post){
        ideaPostService.updatePost(id, post);
        return ResponseEntity.ok(new Success(
                "Post was changed successfully",
                new Date())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id){
        ideaPostService.deletePost(id);
        return ResponseEntity.ok(new Success("The post was deleted successfully",
                new Date()
        ));
    }

    @PostMapping("{id}/like")
    public ResponseEntity<?> likePost(@PathVariable("id") Long id, Principal principal){
        ideaPostService.likePost(id, principal);
        return ResponseEntity.ok(new Success("Like was added successfully",
                new Date()
        ));
    }

    @DeleteMapping("{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable("id") Long id, Principal principal){
        ideaPostService.unlikePost(id, principal);
        return ResponseEntity.ok(new Success("Like was removed successfully",
                new Date()
        ));
    }

    @PostMapping("{id}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable("id") Long id, Principal principal){
        ideaPostService.dislikePost(id, principal);
        return ResponseEntity.ok(new Success("DisLike was added successfully",
                new Date()
        ));
    }

    @DeleteMapping("{id}/dislike")
    public ResponseEntity<?> undislikePost(@PathVariable("id") Long id, Principal principal){
        ideaPostService.undislikePost(id, principal);
        return ResponseEntity.ok(new Success("Dislike was removed successfully",
                new Date()
        ));
    }

    @GetMapping
    public ResponseEntity<?> showPosts(@RequestParam(name = "page") Integer page, @RequestParam(name = "page_size") Integer pageSize, @RequestBody FilterDto filterDto){
        List<IdeaPostDto> resultPosts = ideaPostService.getPosts(page, pageSize, filterDto);
        return ResponseEntity.ok(resultPosts);
    }

    @GetMapping("/filters")
    public ResponseEntity<?> showFilters(){
        return ResponseEntity.ok(ideaPostService.getFilters());
    }
}
