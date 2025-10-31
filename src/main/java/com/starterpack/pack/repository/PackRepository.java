package com.starterpack.pack.repository;

import com.starterpack.pack.entity.Pack;
import java.time.LocalDateTime;
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
    @Query("""
        UPDATE Pack p
        SET p.packCommentCount =
            CASE WHEN p.packCommentCount > 0 THEN p.packCommentCount - 1 ELSE 0 END
        WHERE p.id = :id
    """)
    void decrementCommentCount(@Param("id") Long id);

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

    // ── N+1 제거용: 배치 집계 (IN + GROUP BY) ──

    @Query("""
      select pl.pack.id, count(pl)
      from com.starterpack.pack.entity.PackLike pl
      where pl.pack.id in :ids and pl.createdAt >= :from
      group by pl.pack.id
    """)
    List<Object[]> countLikesSinceIn(@Param("ids") List<Long> ids,
            @Param("from") LocalDateTime from);

    @Query("""
      select pl.pack.id, count(pl)
      from com.starterpack.pack.entity.PackLike pl
      where pl.pack.id in :ids and pl.createdAt >= :from and pl.createdAt < :to
      group by pl.pack.id
    """)
    List<Object[]> countLikesBetweenIn(@Param("ids") List<Long> ids,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
      select pl.pack.id, max(pl.createdAt)
      from com.starterpack.pack.entity.PackLike pl
      where pl.pack.id in :ids
      group by pl.pack.id
    """)
    List<Object[]> findLastLikeTimeIn(@Param("ids") List<Long> ids);

    @Query("""
      select pl.pack.id, pl.createdAt
      from com.starterpack.pack.entity.PackLike pl
      where pl.pack.id in :ids and pl.createdAt >= :from
      order by pl.pack.id asc, pl.createdAt desc
    """)
    List<Object[]> findLikeTimesSinceIn(@Param("ids") List<Long> ids,
            @Param("from") LocalDateTime from);

    // 윈도우 함수로 pack별 최신 N개 타임스탬프
    @Query(value = """
      SELECT pack_id, created_at
      FROM (
        SELECT pl.pack_id, pl.created_at,
               ROW_NUMBER() OVER (PARTITION BY pl.pack_id ORDER BY pl.created_at DESC) AS rn
        FROM pack_like pl
        WHERE pl.pack_id IN (:ids)
      ) t
      WHERE t.rn <= :n
      ORDER BY pack_id, created_at DESC
    """, nativeQuery = true)
    List<Object[]> findRecentLikeTimesTopNIn(@Param("ids") List<Long> ids, @Param("n") int n);
}
