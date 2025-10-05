package com.starterpack.feed.repository;

import com.starterpack.feed.entity.Feed;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long>, JpaSpecificationExecutor<Feed> {
    @EntityGraph(attributePaths = {"user", "category"})
    Optional<Feed> findWithDetailsById(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = {"user", "category"})
    Page<Feed> findAll(Pageable pageable);

    @Modifying
    @Query("UPDATE Feed f SET f.likeCount = f.likeCount + 1 WHERE f.id = :feedId")
    void incrementLikeCount(@Param("feedId") Long id);

    @Modifying
    @Query("UPDATE Feed f SET f.likeCount = f.likeCount - 1 WHERE f.id = :feedId")
    void decrementLikeCount(@Param("feedId") Long id);

    @EntityGraph(attributePaths = {"user", "category"})
    Page<Feed> findAll(Specification<Feed> spec, Pageable pageable);

    @Modifying
    @Query("UPDATE Feed f SET f.bookmarkCount = f.bookmarkCount + 1 WHERE f.id = :feedId")
    void incrementBookmarkCount(@Param("feedId") Long id);

    @Modifying
    @Query("UPDATE Feed f SET f.bookmarkCount = f.bookmarkCount - 1 WHERE f.id = :feedId")
    void decrementBookmarkCount(@Param("feedId") Long id);

    @Modifying
    @Query("UPDATE Feed f SET f.commentCount = f.commentCount + 1 WHERE f.id = :feedId")
    void incrementCommentCount(@Param("feedId") Long id);

    @Modifying
    @Query("UPDATE Feed f SET f.commentCount = f.commentCount - 1 WHERE f.id = :feedId")
    void decrementCommentCount(@Param("feedId") Long id);
}
