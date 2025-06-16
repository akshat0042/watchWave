package org.learn.watchwave.subscriptions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.learn.watchwave.subscriptions.model.Subscribe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, UUID> {
    Optional<Subscribe> findBySubscriberIdAndCreatorId(UUID subscriberId, UUID creatorId);
    List<Subscribe> findBySubscriberIdOrderBySubscribedAtDesc(UUID subscriberId);
    List<Subscribe> findByCreatorIdOrderBySubscribedAtDesc(UUID creatorId);
    void deleteBySubscriberIdAndCreatorId(UUID subscriberId, UUID creatorId);
    boolean existsBySubscriberIdAndCreatorId(UUID subscriberId, UUID creatorId);
}
