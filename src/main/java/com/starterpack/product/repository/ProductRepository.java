package com.starterpack.product.repository;

import com.starterpack.product.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId);
    
    // 페이지네이션을 지원하는 메서드들
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    @Override
    List<Product> findAll();
}
