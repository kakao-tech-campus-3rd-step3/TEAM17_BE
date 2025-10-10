package com.starterpack.pack.repository;

import com.starterpack.pack.entity.PackComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackCommentRepository extends JpaRepository<PackComment, Long> {

    @EntityGraph(attributePaths = {"author", "parent"})
    Page<PackComment> findByPackId(Long packId, Pageable pageable);
}
