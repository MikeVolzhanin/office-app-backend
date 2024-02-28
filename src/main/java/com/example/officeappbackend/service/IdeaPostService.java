package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.IdeaPost;
import com.example.officeappbackend.dto.PublishPostDto;
import com.example.officeappbackend.repositories.IdeaPostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdeaPostService {
    private final IdeaPostRepository ideaPostRepository;
    private final UserService userService;
    private final OfficeService officeService;
    public void publishPost(PublishPostDto post){
    }

    public String convertUrlListToString(List<String> list){
        String urls = String.join(",", list);
        return urls;
    }

}
