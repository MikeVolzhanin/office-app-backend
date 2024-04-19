package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.*;
import com.example.officeappbackend.dto.CommentDto;
import com.example.officeappbackend.dto.ResponseCommentDto;
import com.example.officeappbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final IdeaPostRepository ideaPostRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final CommentDislikesRepository commentDislikesRepository;
    private final UserService userService;

    public ResponseEntity<?> publishComment(Long id, CommentDto commentDto, Principal principal){
        Comment comment = new Comment();
        IdeaPost ideaPost = ideaPostRepository.findById(id).orElse(null);
        if(ideaPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Integer commentsCount = ideaPost.getCommentsCount();
        ideaPost.setCommentsCount(++commentsCount);

        comment.setIdeaPost(ideaPost);
        comment.setAuthor(userRepository.findByEmail(principal.getName()).orElse(null));
        comment.setContent(commentDto.getContent());
        comment.setAttachedImage(commentDto.getAttachedImage());
        comment.setDate(new Date());
        comment.setDislikesCount(0);
        comment.setLikesCount(0);

        commentRepository.save(comment);
        ideaPostRepository.save(ideaPost);

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

    public ResponseEntity<?> showCommentsByFilter(Long PostId, Integer page, Integer pageSize, Principal principal, Integer filter){
        IdeaPost ideaPost = ideaPostRepository.findById(PostId).orElse(null);
        List<ResponseCommentDto> comments = new ArrayList<>(commentRepository.findByIdeaPost(ideaPost).stream().map((Comment comment) -> convertToResComDto(comment, principal)).toList());
        if(ideaPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        switch (filter){
            case 1 : comments.sort((com1, com2) -> com2.getDate().compareTo(com1.getDate()));
            case 2 : comments.sort(Comparator.comparing(ResponseCommentDto::getDate));
            case 3 : comments.sort((com1, com2) -> {
                Integer sum1 = com1.getDislikesCount() + com1.getLikesCount();
                Integer sum2 = com2.getDislikesCount() + com2.getLikesCount();
                return sum1.compareTo(sum2);
            });
            case 4 : comments.sort((com1, com2) -> {
                Integer sum1 = com1.getDislikesCount() + com1.getLikesCount();
                Integer sum2 = com2.getDislikesCount() + com2.getLikesCount();
                return sum2.compareTo(sum1);
            });
        }

        return generatePages(comments, page, pageSize, principal);
    }

    public ResponseEntity<?> showComments(Long PostId, Integer page, Integer pageSize, Principal principal){
        IdeaPost ideaPost = ideaPostRepository.findById(PostId).orElse(null);
        if(ideaPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<ResponseCommentDto> comments = commentRepository.findByIdeaPost(ideaPost).stream().map((Comment comment) -> convertToResComDto(comment, principal)).toList();

        return generatePages(comments, page, pageSize, principal);
    }

    public ResponseEntity<?> generatePages(List<ResponseCommentDto> comments, Integer page, Integer pageSize, Principal principal){
        int items = comments.size();
        int pages = (int) Math.ceil((double) items / pageSize);

        if(pages < page){
            System.out.println("The number of page is less then required");
            return ResponseEntity.ok(new ArrayList<>());
        }

        int fromInd = pageSize * (page - 1);
        int toInd = fromInd + pageSize;

        if(toInd > items)
            toInd = items;

        if(items == 1)
            return ResponseEntity.ok(List.of(comments.get(0)));

        return ResponseEntity.ok(comments.subList(fromInd, toInd));
    }
    public ResponseCommentDto convertToResComDto(Comment comment, Principal principal){
        ResponseCommentDto responseCommentDto = new ResponseCommentDto();
        User author = userRepository.findByEmail(principal.getName()).orElse(null);

        responseCommentDto.setId(comment.getId());
        responseCommentDto.setAuthor(userService.convertToIdeaAuthor(comment.getAuthor()));
        responseCommentDto.setContent(comment.getContent());
        responseCommentDto.setAttachedImage(comment.getAttachedImage());
        responseCommentDto.setDate(comment.getDate());
        responseCommentDto.setLikesCount(comment.getLikesCount());
        responseCommentDto.setIsLikePressed(commentLikesRepository.findByAuthorAndComment(author, comment).isPresent());
        responseCommentDto.setDislikesCount(comment.getDislikesCount());
        responseCommentDto.setIsDislikePressed(commentDislikesRepository.findByAuthorAndComment(author, comment).isPresent());

        return responseCommentDto;
    }
}
