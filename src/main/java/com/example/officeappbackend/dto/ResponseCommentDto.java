package com.example.officeappbackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseCommentDto {
    private Long Id;
    private IdeaAuthor author;
    private String content;
    private String attachedImage;
    private Date date;
    private Boolean isLikePressed;
    private Integer likesCount;
    private Boolean isDislikePressed;
    private Integer dislikesCount;
}
