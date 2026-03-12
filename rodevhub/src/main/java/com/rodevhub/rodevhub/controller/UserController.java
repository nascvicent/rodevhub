package com.rodevhub.rodevhub.controller;
import com.rodevhub.rodevhub.dto.ProjectDTO;
import com.rodevhub.rodevhub.dto.SkillDTO;
import com.rodevhub.rodevhub.dto.UpdateProfileRequest;
import com.rodevhub.rodevhub.dto.UserProfileResponse;
import com.rodevhub.rodevhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/profile/{username}
     * Público — qualquer um pode ver o perfil de um dev
     */
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String username) {
        UserProfileResponse profile = userService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * GET /api/users/me
     * Protegido — retorna o perfil do usuário logado
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = userService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/users/me
     * Protegido — atualiza o perfil do usuário logado
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse profile = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/users/me/skills
     * Protegido — substitui todas as skills do usuário logado
     */
    @PutMapping("/me/skills")
    public ResponseEntity<UserProfileResponse> updateSkills(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody List<SkillDTO> skills
    ) {
        UserProfileResponse profile = userService.updateSkills(userDetails.getUsername(), skills);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/projects")
    public ResponseEntity<List<ProjectDTO>> updateProjects(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<ProjectDTO> projects) {
        List<ProjectDTO> updated = userService.updateProjects(userDetails.getUsername(), projects);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/me/projects")
    public ResponseEntity<ProjectDTO> addProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectDTO project) {
        ProjectDTO created = userService.addProject(userDetails.getUsername(), project);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/me/projects/{id}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        userService.deleteProject(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}