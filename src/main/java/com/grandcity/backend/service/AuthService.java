package com.grandcity.backend.service;

import com.grandcity.backend.common.exception.BadRequestException;
import com.grandcity.backend.repository.AdminUserRepository;
import com.grandcity.backend.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BadRequestException("Username and password are required");
        }

        var admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(admin.getUsername());
    }
}
