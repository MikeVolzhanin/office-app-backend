package com.example.officeappbackend.controllers;

import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.service.CommentService;
import com.example.officeappbackend.service.IdeaPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class IdeaPostController {
    private final IdeaPostService ideaPostService;
    private final CommentService commentService;
    @PostMapping
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
        List<IdeaPostDto> posts = ideaPostService.getPosts(page, pageSize, filterDto, principal, authorId, null);
        if(posts == null)
            return ResponseEntity.ok(new ArrayList<>());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id, Principal principal){
        if(ideaPostService.findPostById(id, principal) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ideaPostService.findPostById(id, principal));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable("id") Long id, @RequestBody EditPostDto post){
        return ideaPostService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id){
        return ideaPostService.deletePost(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable("id") Long id, Principal principal){
        return ideaPostService.likePost(id, principal);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable("id") Long id, Principal principal){
        return ideaPostService.unlikePost(id, principal);
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable("id") Long id, Principal principal){
        return ideaPostService.dislikePost(id, principal);
    }

    @DeleteMapping("/{id}/dislike")
    public ResponseEntity<?> undislikePost(@PathVariable("id") Long id, Principal principal){
        return ideaPostService.undislikePost(id, principal);
    }

    @GetMapping
    public ResponseEntity<?> showPosts(@RequestParam(name="office") Long[] office, @RequestParam(name = "search", required = false) String text, @RequestParam(name="sorting_filter", required = false) Integer sortingFilter,
                                       @RequestParam(name = "page") Integer page, @RequestParam(name = "page_size") Integer pageSize, Principal principal){
        FilterDto filterDto = new FilterDto();
        filterDto.setOfficesId(List.of(office));
        filterDto.setSortingFilterId(sortingFilter);    
        filterDto.setText(text);
        if(ideaPostService.getPosts(page, pageSize, filterDto, principal, null, null) == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        List<IdeaPostDto> resultPosts = ideaPostService.getPosts(page, pageSize, filterDto, principal, null, null);
        return ResponseEntity.ok(resultPosts);
    }
    @GetMapping("/favourite")
    ResponseEntity<?> showFavouritePosts(@RequestParam(name="office") Long[] office, @RequestParam(name = "search", required = false) String text, @RequestParam(name="sorting_filter", required = false) Integer sortingFilter,
                                                     @RequestParam(name = "page") Integer page, @RequestParam(name = "page_size") Integer pageSize, Principal principal){
        FilterDto filterDto = new FilterDto();
        filterDto.setOfficesId(List.of(office));
        filterDto.setSortingFilterId(sortingFilter);
        filterDto.setText(text);
        List<IdeaPostDto> resultPosts = ideaPostService.showFavouritePosts(page, pageSize, filterDto, principal);
        if(resultPosts == null){
           return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
       }
        return ResponseEntity.ok(resultPosts);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> publishComment(@PathVariable Long id, @RequestBody CommentDto commentDto, Principal principal){
        return commentService.publishComment(id, commentDto, principal);
    }

    @PostMapping("/{id}/comments/{commentsId}/like")
    public ResponseEntity<?> likeComment(@PathVariable("id") Long PostId, @PathVariable("commentsId") Long CommentsId, Principal principal){
        return commentService.likeOrDislikeComment(PostId, CommentsId, principal, "like");
    }

    @DeleteMapping("/{id}/comments/{commentsId}/like")
    public ResponseEntity<?> unlikeComment(@PathVariable("id") Long PostId, @PathVariable("commentsId") Long CommentsId, Principal principal){
        return commentService.unlikeOrUndislikeComment(PostId, CommentsId, principal, "unlike");
    }

    @PostMapping("/{id}/comments/{commentsId}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable("id") Long PostId, @PathVariable("commentsId") Long CommentsId, Principal principal){
        return commentService.likeOrDislikeComment(PostId, CommentsId, principal, "dislike");
    }

    @DeleteMapping("/{id}/comments/{commentsId}/dislike")
    public ResponseEntity<?> undislikeComment(@PathVariable("id") Long PostId, @PathVariable("commentsId") Long CommentsId, Principal principal) {
        return commentService.unlikeOrUndislikeComment(PostId, CommentsId, principal, "undislike");
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> showComments(@PathVariable("id") Long PostId,
                                          @RequestParam(name = "page") Integer page, @RequestParam(name = "page_size") Integer pageSize,
                                          Principal principal ){
            return commentService.showComments(PostId, page, pageSize, principal);
    }

    @GetMapping("/filters")
    public ResponseEntity<?> showFilters(){
        Filters filters = ideaPostService.getFilters();
        return ResponseEntity.ok(filters);
    }
}
