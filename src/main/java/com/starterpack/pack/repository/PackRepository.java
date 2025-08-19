package com.starterpack.pack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackRepository extends JpaRepository<Pack, Long> {
    List<Pack> findByCategory_Id(Long categoryId);

    // Pack을 조회할 때 products까지 함께 로딩하고 싶을 때
    @Query("select distinct p from Pack p left join fetch p.products where p.id = :id")
    Optional<Pack> findWithProductsById(@Param("id") Long id);
}
