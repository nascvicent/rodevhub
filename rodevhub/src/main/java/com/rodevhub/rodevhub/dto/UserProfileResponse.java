package com.rodevhub.rodevhub.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserProfileResponse {

    private List<ProjectDTO> projects;

    private UUID id;
    private String username;
    private String displayName;
    private String tagline;
    private String bio;
    private String location;
    private String avatarUrl;
    private String bannerUrl;
    private Boolean verified;
    private Boolean available;
    private Set<String> roles;
    private List<SkillDTO> skills;
    private String robloxUsername;
    private String discordTag;
    private String twitterHandle;
    private String githubUsername;
    private LocalDateTime createdAt;

}
