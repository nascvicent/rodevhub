package com.rodevhub.rodevhub.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID; // Importante: Importar UUID

@Data
public class PostResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private int likesCount;
    private boolean likedByMe;
    private Integer comments;
    private List<String> tags;

    private AuthorDTO author;

    @Data
    public static class AuthorDTO {
        private UUID id;
        private String displayName;
        private String username;
        private String avatarColor;
        private String tagline;
        private String avatarUrl;
    }
}