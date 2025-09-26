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

    List<Pack> findByCategory_Id(Long categoryId);

    @Override
    @EntityGraph(attributePaths = {"products", "category"})
    List<Pack> findAll();

    @Query("""
        select distinct p
        from Pack p
        left join fetch p.products
        left join fetch p.category
        where p.category.id = :categoryId
    """)
    List<Pack> findAllByCategoryIdWithProducts(@Param("categoryId") Long categoryId);

    @Query("""
        select distinct p
        from Pack p
        left join fetch p.products
        left join fetch p.category
        where p.id = :id
    """)
    Optional<Pack> findWithProductsById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packLikeCount = p.packLikeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packLikeCount = p.packLikeCount - 1 WHERE p.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Query("UPDATE Pack p SET p.packBookmarkCount = p.packBookmarkCount + 1 WHERE p.id = :id")
    void incrementPackBookmarkCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pack p SET p.packBookmarkCount = p.packBookmarkCount - 1 WHERE p.id = :id")
    void decrementPackBookmarkCount(@Param("id") Long id);
}
