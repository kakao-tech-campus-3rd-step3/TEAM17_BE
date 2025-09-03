package com.starterpack.product.repository;

import com.starterpack.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    @Override
    List<Product> findAll();
}
