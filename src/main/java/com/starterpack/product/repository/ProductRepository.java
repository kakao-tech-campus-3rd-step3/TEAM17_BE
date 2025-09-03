package com.starterpack.product.repository;

import com.starterpack.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId);
    
    // 정렬을 위한 메서드들
    List<Product> findAllByOrderByIdAsc();
    List<Product> findAllByOrderByNameAsc();
    List<Product> findAllByOrderByCostAsc();
    List<Product> findAllByOrderByCostDesc();
    List<Product> findAllByOrderByCategoryNameAsc();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    @Override
    List<Product> findAll();
}
