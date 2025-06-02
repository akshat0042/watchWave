package org.learn.watchwave.auth.service.impl;

import org.learn.watchwave.auth.dto.request.AuthRequest;
import org.learn.watchwave.auth.dto.RegisterRequest;
import org.learn.watchwave.auth.dto.response.AuthResponse;
import org.learn.watchwave.auth.model.entity.Role;
import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.model.entity.UserProfile;
import org.learn.watchwave.auth.model.entity.UserRole;
import org.learn.watchwave.auth.model.id.UserRoleId;
import org.learn.watchwave.auth.repository.RoleRepository;
import org.learn.watchwave.auth.repository.UserRepository;
import org.learn.watchwave.auth.repository.UserRoleRepository;
import org.learn.watchwave.auth.service.interfaces.AuthService;
import org.learn.watchwave.auth.service.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;           // ADD THIS
    private final UserRoleRepository userRoleRepository;   // ADD THIS

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,           // ADD THIS
                           UserRoleRepository userRoleRepository) { // ADD THIS
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;               // ADD THIS
        this.userRoleRepository = userRoleRepository;       // ADD THIS
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        try {
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.isBlocked()) {
                throw new RuntimeException("Account is blocked. Please contact support.");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword())
            );

            String token = jwtService.generateToken(user);
            return new AuthResponse(token, user.getUsername(), user.getEmail());
        } catch (AuthenticationException ex) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = createUser(registerRequest);
        User savedUser = userRepository.save(user);

        assignDefaultRole(savedUser);

        String token = jwtService.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail());
    }

    private void assignDefaultRole(User user) {
        try {
            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found. Please ensure DataInitializer has run."));

            UserRole userRoleEntity = new UserRole();
            UserRoleId userRoleId = new UserRoleId(user.getId(), userRole.getId());
            userRoleEntity.setId(userRoleId);
            userRoleEntity.setUser(user);
            userRoleEntity.setRole(userRole);

            userRoleRepository.save(userRoleEntity);

            System.out.println("✅ Assigned USER role to: " + user.getUsername());
        } catch (Exception e) {
            System.err.println("❌ Failed to assign role to " + user.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public AuthResponse refreshToken(String token) {
        if (jwtService.isTokenValid(token)) {
            UUID userId = jwtService.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.isBlocked()) {
                throw new RuntimeException("Account is blocked. Please contact support.");
            }

            String newToken = jwtService.generateToken(user);
            return new AuthResponse(newToken, user.getUsername(), user.getEmail());
        }
        throw new RuntimeException("Invalid or expired token");
    }

    @Override
    public void logout(String token) {
        //Might do later
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    private User createUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setUpdatedAt(Timestamp.from(Instant.now()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        if (hasProfileData(registerRequest)) {
            UserProfile userProfile = createUserProfile(registerRequest, user);
            user.setUserProfile(userProfile);
        }

        return user;
    }

    private boolean hasProfileData(RegisterRequest registerRequest) {
        return registerRequest.getGender() != null ||
                registerRequest.getBirthdate() != null ||
                registerRequest.getLocation() != null ||
                registerRequest.getBio() != null;
    }

    private UserProfile createUserProfile(RegisterRequest registerRequest, User user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setGender(registerRequest.getGender());
        userProfile.setBirthdate(registerRequest.getBirthdate());
        userProfile.setLocation(registerRequest.getLocation());
        userProfile.setBio(registerRequest.getBio());
        return userProfile;
    }
}
