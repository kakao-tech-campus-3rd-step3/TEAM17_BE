package com.starterpack.pack.repository;

import com.starterpack.pack.entity.Pack;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PackRepository extends JpaRepository<Pack, Long> {
    @Override
    @EntityGraph(attributePaths = {"items", "category", "member"})
    List<Pack> findAll();

    @Query("""
        select distinct p
        from Pack p
        left join fetch p.items
        left join fetch p.category
        left join fetch p.member
        where p.category.id = :categoryId
    """)
    List<Pack> findAllByCategoryIdWithItems(@Param("categoryId") Long categoryId);

    @Query("""
        select distinct p
        from Pack p
        left join fetch p.items
        left join fetch p.category
        left join fetch p.member
        where p.id = :id
    """)
    Optional<Pack> findWithItemsById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packLikeCount = p.packLikeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packLikeCount = p.packLikeCount - 1 WHERE p.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packBookmarkCount = p.packBookmarkCount + 1 WHERE p.id = :id")
    void incrementPackBookmarkCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packBookmarkCount = p.packBookmarkCount - 1 WHERE p.id = :id")
    void decrementPackBookmarkCount(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Pack p SET p.packCommentCount = p.packCommentCount + 1 WHERE p.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Pack p SET p.packCommentCount = CASE WHEN p.packCommentCount > 0 THEN p.packCommentCount - 1 ELSE 0 END WHERE p.id = :id")
    void decrementCommentCount(@Param("id") Long id);
}
