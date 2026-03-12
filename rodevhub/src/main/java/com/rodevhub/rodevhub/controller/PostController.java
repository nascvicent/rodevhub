package com.rodevhub.rodevhub.controller;

import com.rodevhub.rodevhub.dto.CreatePostRequest;
import com.rodevhub.rodevhub.dto.PostResponse;
import com.rodevhub.rodevhub.entity.Post;
import com.rodevhub.rodevhub.entity.User;
import com.rodevhub.rodevhub.exception.ResourceNotFoundException;
import com.rodevhub.rodevhub.repository.PostRepository;
import com.rodevhub.rodevhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<PostResponse> getAllPosts(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> mapToDTO(post, user.getId()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        // Busca o usuário logado pelo email/username do token
        User author = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Post post = new Post();
        post.setContent(request.getContent());
        post.setTags(request.getTags());
        post.setMediaUrl(request.getMediaUrl());
        post.setAuthor(author);

        Post savedPost = postRepository.save(post);
        return ResponseEntity.ok(mapToDTO(savedPost, author.getId()));
    }

    private PostResponse mapToDTO(Post post, UUID currentUserId) {
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikesCount(post.getLikesCount());
        dto.setLikedByMe(post.getLikedByUserIds().contains(currentUserId));
        dto.setComments(post.getCommentsCount());
        dto.setTags(post.getTags());

        PostResponse.AuthorDTO authorDTO = new PostResponse.AuthorDTO();
        authorDTO.setId(post.getAuthor().getId());


        if (post.getAuthor().getDisplayName() != null && !post.getAuthor().getDisplayName().isEmpty()) {
            authorDTO.setDisplayName(post.getAuthor().getDisplayName());
        } else {
            authorDTO.setDisplayName(post.getAuthor().getUsername());
        }

        authorDTO.setUsername(post.getAuthor().getUsername());
        authorDTO.setAvatarUrl(post.getAuthor().getAvatarUrl());

        if (post.getAuthor().getTagline() != null && !post.getAuthor().getTagline().isEmpty()) {
            authorDTO.setTagline(post.getAuthor().getTagline());
        } else {
            String roleName = post.getAuthor().getRoles().stream()
                    .findFirst()
                    .map(Enum::name)
                    .orElse("MEMBER");
            authorDTO.setTagline(roleName);
        }
        authorDTO.setAvatarColor("#3c6eff");
        dto.setAuthor(authorDTO);
        return dto;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        boolean liked;
        if (post.getLikedByUserIds().contains(user.getId())) {
            post.getLikedByUserIds().remove(user.getId());
            liked = false;
        } else {
            post.getLikedByUserIds().add(user.getId());
            liked = true;
        }

        postRepository.save(post);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likesCount", post.getLikesCount()
        ));
    }
}