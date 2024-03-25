package com.example.officeappbackend.controllers;

import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.exceptions.AppError;
import com.example.officeappbackend.service.IdeaPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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

    @GetMapping("/by-author-id/{authorId}")
    public ResponseEntity<?> getPostByAuthorId(@PathVariable("authorId") Long authorId, @RequestParam(name = "page") Integer page, @RequestParam(name = "page_size") Integer pageSize, Principal principal){
        FilterDto filterDto = new FilterDto();
        filterDto.setSortingFilterId(null);
        filterDto.setText("");
        filterDto.setOfficesId(List.of(1L, 2L, 3L, 4L, 5L));
        List<IdeaPostDto> posts = ideaPostService.getPosts(page, pageSize, filterDto, principal, authorId);
        if(posts == null)
            return ResponseEntity.ok(new ArrayList<>());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id, Principal principal){
        if(ideaPostService.findPostById(id, principal) == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(ideaPostService.findPostById(id, principal));
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
        return ideaPostService.likePost(id, principal);
    }

    @DeleteMapping("{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable("id") Long id, Principal principal){
        ideaPostService.unlikePost(id, principal);
        return  ResponseEntity.ok(new Success("Like was removed successfully",
                new Date()
        ));
    }

    @PostMapping("{id}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable("id") Long id, Principal principal){
        return ideaPostService.dislikePost(id, principal);
    }

    @DeleteMapping("{id}/dislike")
    public ResponseEntity<?> undislikePost(@PathVariable("id") Long id, Principal principal){
        ideaPostService.undislikePost(id, principal);
        return ResponseEntity.ok(new Success("Dislike was removed successfully",
                new Date()
        ));
    }

    @GetMapping
    public ResponseEntity<?> showPosts(@RequestParam(name="office") Long[] office, @RequestParam(name = "search", required = false) String text, @RequestParam(name="sorting_filter", required = false) Integer sortingFilter,
                                       @RequestParam(name = "page") Integer page, @RequestParam(name = "page_size") Integer pageSize, Principal principal){
        FilterDto filterDto = new FilterDto();
        filterDto.setOfficesId(List.of(office));
        filterDto.setSortingFilterId(sortingFilter);    
        filterDto.setText(text);
        if(ideaPostService.getPosts(page, pageSize, filterDto, principal, null) == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        List<IdeaPostDto> resultPosts = ideaPostService.getPosts(page, pageSize, filterDto, principal, null);
        return ResponseEntity.ok(resultPosts);
    }

    @GetMapping("/filters")
    public ResponseEntity<?> showFilters(){
        Filters filters = ideaPostService.getFilters();
        return ResponseEntity.ok(filters);
    }
}
