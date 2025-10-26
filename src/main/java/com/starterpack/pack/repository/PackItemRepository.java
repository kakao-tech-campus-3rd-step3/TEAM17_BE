package com.starterpack.pack.repository;

import com.starterpack.pack.entity.PackItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackItemRepository extends JpaRepository<PackItem, Long> {
}
