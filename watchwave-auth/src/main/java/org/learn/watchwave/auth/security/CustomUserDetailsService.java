package org.learn.watchwave.auth.security;

import org.learn.watchwave.auth.model.entity.User;
import org.learn.watchwave.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return createUserDetails(user);
    }

    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        User user = userRepository.findByIdWithRoles(userId)  // Use JOIN FETCH method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return createUserDetails(user);
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        try {
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                authorities = user.getRoles().stream()
                        .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName()))
                        .collect(Collectors.toList());
            } else {
                // Assign default USER role if no roles exist
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load user roles for " + user.getUsername() + ": " + e.getMessage());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}
