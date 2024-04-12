package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.*;
import com.example.officeappbackend.dto.CommentDto;
import com.example.officeappbackend.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Date;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final IdeaPostRepository ideaPostRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final CommentDislikesRepository commentDislikesRepository;
    public ResponseEntity<?> publishComment(Long id, CommentDto commentDto, Principal principal){
        Comment comment = new Comment();
        IdeaPost ideaPost = ideaPostRepository.findById(id).orElse(null);
        if(ideaPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        comment.setIdeaPost(ideaPost);
        comment.setAuthor(userRepository.findByEmail(principal.getName()).orElse(null));
        comment.setContent(commentDto.getContent());
        comment.setAttachedImage(commentDto.getAttachedImage());
        comment.setDate(new Date());
        comment.setDislikesCount(0);
        comment.setLikesCount(0);

        commentRepository.save(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> likeOrDislikeComment(Long PostId, Long CommentId, Principal principal, String type){
        Comment comment = commentRepository.findById(CommentId).orElse(null);
        IdeaPost ideaPost = ideaPostRepository.findById(PostId).orElse(null);
        User author = userRepository.findByEmail(principal.getName()).orElse(null);

        if(comment == null || ideaPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(type.equals("like") && commentLikesRepository.findByAuthorAndComment(author, comment).isEmpty()){

            if(commentDislikesRepository.findByAuthorAndComment(author, comment).isPresent())
                unlikeOrUndislikeComment(PostId, CommentId, principal, "undislike");

            CommentLikes commentLikes = new CommentLikes();
            commentLikes.setComment(comment);
            commentLikes.setAuthor(userRepository.findByEmail(principal.getName()).orElse(null));

            Integer likesCount = comment.getLikesCount();
            comment.setLikesCount(++likesCount);

            commentLikesRepository.save(commentLikes);
            commentRepository.save(comment);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        if(type.equals("dislike") && commentDislikesRepository.findByAuthorAndComment(author, comment).isEmpty()){

            if(commentLikesRepository.findByAuthorAndComment(author, comment).isPresent())
                unlikeOrUndislikeComment(PostId, CommentId, principal, "unlike");

            CommentDislikes commentDislikes = new CommentDislikes();
            commentDislikes.setComment(comment);
            commentDislikes.setAuthor(userRepository.findByEmail(principal.getName()).orElse(null));

            Integer dislikesCount = comment.getDislikesCount();
            comment.setDislikesCount(++dislikesCount);

            commentDislikesRepository.save(commentDislikes);
            commentRepository.save(comment);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<?> unlikeOrUndislikeComment(Long PostId, Long CommentId, Principal principal, String type){
        Comment comment = commentRepository.findById(CommentId).orElse(null);
        IdeaPost ideaPost = ideaPostRepository.findById(PostId).orElse(null);
        User author = userRepository.findByEmail(principal.getName()).orElse(null);

        if(comment == null || ideaPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(type.equals("unlike") && commentLikesRepository.findByAuthorAndComment(author, comment).isPresent()){
            Integer likesCount = comment.getLikesCount();
            comment.setLikesCount(--likesCount);

            commentRepository.save(comment);

            commentLikesRepository.deleteByAuthorAndComment(author, comment);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        if(type.equals("undislike") && commentDislikesRepository.findByAuthorAndComment(author, comment).isPresent()){

            Integer dislikesCount = comment.getDislikesCount();
            comment.setDislikesCount(--dislikesCount);

            commentRepository.save(comment);

            commentDislikesRepository.deleteByAuthorAndComment(author, comment);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
