package com.rodevhub.rodevhub.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreatePostRequest {
    private String content;
    private String mediaUrl;
    private List<String> tags;
}