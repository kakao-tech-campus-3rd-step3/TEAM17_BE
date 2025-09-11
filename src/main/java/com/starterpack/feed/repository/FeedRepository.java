package com.starterpack.feed.repository;

import com.starterpack.feed.entity.Feed;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query("SELECT f FROM Feed f " +
            "JOIN FETCH f.user " +
            "LEFT JOIN FETCH f.category " +
            "LEFT JOIN FETCH f.feedProducts fp " +
            "LEFT JOIN FETCH fp.product " +
            "WHERE f.id = :id")
    Optional<Feed> findByIdWithDetails(@Param("id") Long id);
}
