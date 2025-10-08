package com.starterpack.hashtag.repository;

import com.starterpack.hashtag.entity.Hashtag;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Set<Hashtag> findAllByNameIn(Set<String> names);
}
