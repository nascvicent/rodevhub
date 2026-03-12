package com.rodevhub.rodevhub.entity;

import com.rodevhub.rodevhub.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String displayName;

    @Column(length = 200)
    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 100)
    private String location;

    @Column(length = 300)
    private String avatarUrl;

    @Column(length = 300)
    private String bannerUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();


    // Social links
    @Column(length = 100)
    private String robloxUsername;

    @Column(length = 100)
    private String discordTag;

    @Column(length = 100)
    private String twitterHandle;

    @Column(length = 100)
    private String githubUsername;

    // OAuth
    @Column(length = 50)
    private String provider;

    @Column(length = 255)
    private String providerId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper para gerenciar skills
    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setUser(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setUser(null);
    }

    public void clearSkills() {
        skills.forEach(s -> s.setUser(null));
        skills.clear();
    }
}