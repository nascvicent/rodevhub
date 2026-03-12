package com.rodevhub.rodevhub.dto;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "Display name máximo 100 caracteres")
    private String displayName;

    @Size(max = 200, message = "Tagline máximo 200 caracteres")
    private String tagline;

    @Size(max = 2000, message = "Bio máximo 2000 caracteres")
    private String bio;

    @Size(max = 100)
    private String location;

    @Size(max = 300)
    private String avatarUrl;

    @Size(max = 300)
    private String bannerUrl;

    private Boolean available;

    @Size(max = 3, message = "Máximo de 3 roles")
    private Set<String> roles;

    // Social links
    @Size(max = 100)
    private String robloxUsername;

    @Size(max = 100)
    private String discordTag;

    @Size(max = 100)
    private String twitterHandle;

    @Size(max = 100)
    private String githubUsername;
}