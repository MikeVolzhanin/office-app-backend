package com.example.officeappbackend.dto;

import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.Entities.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class IdeaPostDto {
    private Long id;
    private String title;
    private String content;
    private Date date;
    private IdeaAuthor ideaAuthor;
    private List<String> attachedImages;
    private OfficeDto office;
    private Integer likesCount;
    private Boolean isLikePressed;
    private Integer dislikesCount;
    private Boolean isDislikePressed;
    private Integer commentsCount;
}
