package com.rodevhub.rodevhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private Set<String> roles;

    public AuthResponse(String token, UUID id, String username, String email,
                        String displayName, Set<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.roles = roles;
    }
}
