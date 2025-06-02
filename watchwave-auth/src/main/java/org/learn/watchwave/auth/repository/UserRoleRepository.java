package org.learn.watchwave.auth.repository;

import org.learn.watchwave.auth.model.entity.UserRole;
import org.learn.watchwave.auth.model.id.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    // Removes a specific role from a user (e.g., demote CREATOR to USER)
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId = :userId AND ur.id.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") UUID userId, @Param("roleId") Integer roleId);

    // Checks if user has a specific role (returns true/false for permission validation)
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur WHERE ur.id.userId = :userId AND ur.id.roleId = :roleId")
    boolean existsByUserIdAndRoleId(@Param("userId") UUID userId, @Param("roleId") Integer roleId);

    // Gets all roles assigned to a specific user (for user profile/dashboard)
    @Query("SELECT ur FROM UserRole ur WHERE ur.id.userId = :userId")
    List<UserRole> findByUserId(@Param("userId") UUID userId);

    // Gets all users who have a specific role (for admin management/role analytics)
    @Query("SELECT ur FROM UserRole ur WHERE ur.id.roleId = :roleId")
    List<UserRole> findByRoleId(@Param("roleId") Integer roleId);
}
