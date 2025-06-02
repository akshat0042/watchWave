package org.learn.watchwave.auth.repository;

import org.learn.watchwave.auth.model.entity.RoleChangeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleChangeRequestRepository extends JpaRepository<RoleChangeRequestEntity, UUID> {
    boolean existsByUserIdAndStatus(UUID userId, String status);
    List<RoleChangeRequestEntity> findByStatus(String status);
    List<RoleChangeRequestEntity> findByUserId(UUID userId);
    List<RoleChangeRequestEntity> findByUserIdOrderByRequestedAtDesc(UUID userId);
}
