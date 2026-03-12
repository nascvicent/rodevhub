package com.rodevhub.rodevhub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String mediaUrl; // Para imagens/vídeos
    private String mediaType; // "IMAGE", "VIDEO", etc.

    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer likesCount = 0;
    private Integer commentsCount = 0;

    // Relacionamento com o Autor (User)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private Set<UUID> likedByUserIds = new HashSet<>();

    public int getLikesCount() {
        return likedByUserIds.size();
    }
}