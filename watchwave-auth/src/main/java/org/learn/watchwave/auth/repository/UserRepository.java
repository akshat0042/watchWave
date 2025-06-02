package org.learn.watchwave.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.learn.watchwave.auth.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByVerificationToken(String token);

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            @Param("username") String username,
            @Param("email") String email,
            Pageable pageable);

    // ADD THESE JOIN FETCH METHODS
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles ur LEFT JOIN FETCH ur.role WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles ur LEFT JOIN FETCH ur.role WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles ur LEFT JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}
