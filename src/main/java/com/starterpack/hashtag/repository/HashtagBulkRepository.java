package com.starterpack.hashtag.repository;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class HashtagBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String Bulk_INSERT_IGNORE_SQL = """
            INSERT IGNORE INTO hashtag (name, usage_count, created_at, updated_at) 
            VALUES (?, 0, NOW(), NOW())
    """;

    @Transactional
    public void saveAllWithInsertIgnore(Set<String> hashtagNames) {
        if (hashtagNames == null || hashtagNames.isEmpty()) {
            return;
        }

        List<Object[]> batchArgs = hashtagNames.stream()
                .map(name -> new Object[]{name})
                .toList();

        jdbcTemplate.batchUpdate(Bulk_INSERT_IGNORE_SQL, batchArgs);
    }

}
