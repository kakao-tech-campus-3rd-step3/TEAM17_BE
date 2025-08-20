package com.starterpack.pack.repository;

import com.starterpack.pack.entity.Pack;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PackRepository extends JpaRepository<Pack, Long> {

    // 기본 쿼리 (지연로딩)
    List<Pack> findByCategory_Id(Long categoryId);

    // 전용 메서드로 명시적으로 페치 전략을 구분
    @EntityGraph(attributePaths = "products")
    List<Pack> findAllWithProducts();

    @EntityGraph(attributePaths = "products")
    List<Pack> findAllByCategoryIdWithProducts(Long categoryId);

    // 상세 조회 시 products까지 함께 로딩하고 싶을 때 사용
    @Query("select distinct p from Pack p left join fetch p.products where p.id = :id")
    Optional<Pack> findWithProductsById(@Param("id") Long id);
}
