package com.rodevhub.rodevhub.service;

import com.rodevhub.rodevhub.dto.ProjectDTO;
import com.rodevhub.rodevhub.dto.SkillDTO;
import com.rodevhub.rodevhub.dto.UpdateProfileRequest;
import com.rodevhub.rodevhub.dto.UserProfileResponse;
import com.rodevhub.rodevhub.entity.Project;
import com.rodevhub.rodevhub.entity.Skill;
import com.rodevhub.rodevhub.entity.User;
import com.rodevhub.rodevhub.enums.UserRole;
import com.rodevhub.rodevhub.exception.BadRequestException;
import com.rodevhub.rodevhub.exception.ResourceNotFoundException;
import com.rodevhub.rodevhub.repository.ProjectRepository;
import com.rodevhub.rodevhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public UserProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        return mapToProfile(user);
    }

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return mapToProfile(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getTagline() != null) user.setTagline(request.getTagline());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getBannerUrl() != null) user.setBannerUrl(request.getBannerUrl());
        if (request.getAvailable() != null) user.setAvailable(request.getAvailable());

        if (request.getRobloxUsername() != null) user.setRobloxUsername(request.getRobloxUsername());
        if (request.getDiscordTag() != null) user.setDiscordTag(request.getDiscordTag());
        if (request.getTwitterHandle() != null) user.setTwitterHandle(request.getTwitterHandle());
        if (request.getGithubUsername() != null) user.setGithubUsername(request.getGithubUsername());

        if (request.getRoles() != null) {
            Set<UserRole> roles = new HashSet<>();
            for (String role : request.getRoles()) {
                try {
                    roles.add(UserRole.valueOf(role.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Role inválida: " + role);
                }
            }
            user.setRoles(roles);
        }

        userRepository.save(user);
        return mapToProfile(user);
    }

    @Transactional
    public UserProfileResponse updateSkills(String email, List<SkillDTO> skillDTOs) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (skillDTOs.size() > 15) {
            throw new BadRequestException("Máximo de 15 skills permitidas");
        }

        user.clearSkills();

        for (SkillDTO dto : skillDTOs) {
            Skill skill = Skill.builder()
                    .name(dto.getName())
                    .level(dto.getLevel() != null ? dto.getLevel() : 50)
                    .build();
            user.addSkill(skill);
        }

        userRepository.save(user);
        return mapToProfile(user);
    }

    // --- MÉTODOS DE PROJETO ADICIONADOS ---

    @Transactional
    public List<ProjectDTO> updateProjects(String email, List<ProjectDTO> projectDTOs) {
        // Buscamos por Email, pois userDetails.getUsername() no seu sistema retorna o Email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        // Remove projetos antigos do usuário
        projectRepository.deleteAll(projectRepository.findByUserOrderByProductionDateDesc(user));

        // Cria novos projetos
        List<Project> projects = projectDTOs.stream().map(dto -> {
            Project p = new Project();
            p.setUser(user);
            p.setName(dto.getName());
            p.setDescription(dto.getDescription());
            p.setLink(dto.getLink());
            p.setProductionDate(dto.getProductionDate());
            return p;
        }).collect(Collectors.toList());

        List<Project> saved = projectRepository.saveAll(projects);

        return saved.stream()
                .map(p -> new ProjectDTO(p.getId(), p.getName(), p.getDescription(), p.getLink(), p.getProductionDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO addProject(String email, ProjectDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (projectRepository.countByUser(user) >= 20) {
            throw new BadRequestException("Máximo de 20 projetos permitido");
        }

        // Certifique-se que o construtor da Entity Project aceita esses parâmetros
        Project project = new Project();
        project.setUser(user);
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setLink(dto.getLink());
        project.setProductionDate(dto.getProductionDate());

        Project saved = projectRepository.save(project);

        return new ProjectDTO(saved.getId(), saved.getName(), saved.getDescription(), saved.getLink(), saved.getProductionDate());
    }

    @Transactional
    public void deleteProject(String email, Long projectId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        // Verificação de segurança: O projeto pertence ao usuário logado?
        if (!project.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Você não tem permissão para deletar este projeto");
        }

        projectRepository.delete(project);
    }

    // --- MÉTODO DE MAPEAMENTO ATUALIZADO ---

    private UserProfileResponse mapToProfile(User user) {
        List<SkillDTO> skillDTOs = user.getSkills().stream()
                .map(skill -> {
                    SkillDTO dto = new SkillDTO();
                    dto.setName(skill.getName());
                    dto.setLevel(skill.getLevel());
                    return dto;
                })
                .collect(Collectors.toList());

        // Busca os projetos associados ao usuário
        List<ProjectDTO> projectDTOs = projectRepository
                .findByUserOrderByProductionDateDesc(user)
                .stream()
                .map(p -> new ProjectDTO(p.getId(), p.getName(), p.getDescription(), p.getLink(), p.getProductionDate()))
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .tagline(user.getTagline())
                .bio(user.getBio())
                .location(user.getLocation())
                .avatarUrl(user.getAvatarUrl())
                .bannerUrl(user.getBannerUrl())
                .verified(user.getVerified())
                .available(user.getAvailable())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .skills(skillDTOs)
                .projects(projectDTOs) // Inserindo a lista de projetos no response
                .robloxUsername(user.getRobloxUsername())
                .discordTag(user.getDiscordTag())
                .twitterHandle(user.getTwitterHandle())
                .githubUsername(user.getGithubUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}