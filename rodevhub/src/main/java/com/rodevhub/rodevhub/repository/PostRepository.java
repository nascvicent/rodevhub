package com.rodevhub.rodevhub.repository;

import com.rodevhub.rodevhub.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Buscar posts ordenados pelo mais recente
    List<Post> findAllByOrderByCreatedAtDesc();
}