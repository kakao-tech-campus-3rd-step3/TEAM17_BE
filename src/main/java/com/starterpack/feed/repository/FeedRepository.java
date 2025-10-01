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
    @Query("SELECT f FROM Feed f " +
            "JOIN FETCH f.user " +
            "LEFT JOIN FETCH f.category " +
            "LEFT JOIN FETCH f.feedProducts fp " +
            "LEFT JOIN FETCH fp.product " +
            "WHERE f.id = :id")
    Optional<Feed> findByIdWithDetails(@Param("id") Long id);

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
}
