package org.learn.watchwave.interactions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.learn.watchwave.interactions.model.entity.Comments;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, UUID> {
    List<Comments> findByVideoIdAndDeletedFalseOrderByCreatedAtAsc(UUID videoId);
    List<Comments> findByParentIdAndDeletedFalseOrderByCreatedAtAsc(UUID parentId);
}
