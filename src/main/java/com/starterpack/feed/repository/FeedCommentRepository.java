package com.starterpack.feed.repository;

import com.starterpack.feed.entity.FeedComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {

    /** 피드별 댓글 페이지 조회 (N+1 방지 위해 author/parent 즉시 로딩) */
    @EntityGraph(attributePaths = {"author", "parent"})
    Page<FeedComment> findByFeedId(Long feedId, Pageable pageable);
}
