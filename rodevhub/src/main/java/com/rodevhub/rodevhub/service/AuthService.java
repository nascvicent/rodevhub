package com.rodevhub.rodevhub.service;

import com.rodevhub.rodevhub.dto.AuthResponse;
import com.rodevhub.rodevhub.dto.LoginRequest;
import com.rodevhub.rodevhub.dto.RegisterRequest;
import com.rodevhub.rodevhub.entity.User;
import com.rodevhub.rodevhub.enums.UserRole;
import com.rodevhub.rodevhub.exception.BadRequestException;
import com.rodevhub.rodevhub.repository.UserRepository;
import com.rodevhub.rodevhub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Verificar duplicados
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Este e-mail já está cadastrado");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Este username já está em uso");
        }

        // Converter roles de String para Enum
        Set<UserRole> roles = new HashSet<>();
        if (request.getRoles() != null) {
            for (String role : request.getRoles()) {
                try {
                    roles.add(UserRole.valueOf(role.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Role inválida: " + role);
                }
            }
        }

        // Criar o usuário
        User user = User.builder()
                .username(request.getUsername().toLowerCase())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getUsername()) // default: mesmo do username
                .provider("local")
                .roles(roles)
                .build();

        userRepository.save(user);

        // Gerar token
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                roles.stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        // Buscar usuário
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Credenciais inválidas"));

        // Gerar token
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}
