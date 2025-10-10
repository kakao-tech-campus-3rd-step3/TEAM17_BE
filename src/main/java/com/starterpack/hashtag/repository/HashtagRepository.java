package com.starterpack.hashtag.repository;

import com.starterpack.hashtag.entity.Hashtag;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Set<Hashtag> findAllByNameIn(Set<String> names);

    @Modifying
    @Query("UPDATE Hashtag h SET h.usageCount = h.usageCount + 1 WHERE h.id IN :ids")
    void bulkIncrementUsageCount(@Param("ids") Set<Long> ids);

    @Modifying
    @Query("UPDATE Hashtag h SET h.usageCount = h.usageCount - 1 WHERE h.id IN :ids AND h.usageCount > 0")
    void bulkDecrementUsageCount(@Param("ids") Set<Long> ids);
}
