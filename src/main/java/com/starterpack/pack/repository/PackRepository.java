package com.starterpack.pack.repository;

import com.starterpack.pack.entity.Pack;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
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

    /** 최근 from 시각 이후 좋아요 수(L24h/L7d 등) */
    @Query("""
        select count(pl) from PackLike pl
        where pl.pack.id = :packId
          and pl.createdAt >= :from
    """)
    long countLikesSince(@Param("packId") Long packId, @Param("from") LocalDateTime from);

    /** 구간 좋아요 수(Lprev24h 등) */
    @Query("""
        select count(pl) from PackLike pl
        where pl.pack.id = :packId
          and pl.createdAt >= :from and pl.createdAt < :to
    """)
    long countLikesBetween(@Param("packId") Long packId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    /** 최신 좋아요 시각 */
    @Query("""
        select max(pl.createdAt) from PackLike pl
        where pl.pack.id = :packId
    """)
    Optional<LocalDateTime> findLastLikeTime(@Param("packId") Long packId);

    /** 최근 N개 좋아요 시각 (Pageable로 제한) */
    @Query("""
        select pl.createdAt from PackLike pl
        where pl.pack.id = :packId
        order by pl.createdAt desc
    """)
    List<LocalDateTime> findRecentLikeTimes(@Param("packId") Long packId, Pageable pageable);

    /** streak 계산용: 기준 시각 이후의 like 시간 모두 */
    @Query("""
        select pl.createdAt from PackLike pl
        where pl.pack.id = :packId and pl.createdAt >= :from
    """)
    List<LocalDateTime> findLikeTimesSince(@Param("packId") Long packId,
            @Param("from") LocalDateTime from);

    // 멤버별 팩 목록 조회
    @Query("""
        select distinct p
        from Pack p
        left join fetch p.items
        left join fetch p.category
        where p.member.userId = :memberId
        order by p.id desc
    """)
    List<Pack> findByMemberId(@Param("memberId") Long memberId);

    // 멤버별 팩 개수 조회
    @Query("""
        select count(p)
        from Pack p
        where p.member.userId = :memberId
    """)
    long countByMemberId(@Param("memberId") Long memberId);

}
