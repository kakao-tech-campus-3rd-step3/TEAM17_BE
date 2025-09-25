package com.starterpack.feed.repository;

import com.starterpack.feed.entity.FeedComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {

    /**
     * 특정 피드의 댓글을 페이지 단위로 조회합니다.
     * 삭제 여부와 관계없이 모두 반환하여 tombstone 처리할 수 있게 합니다.
     */
    Page<FeedComment> findByFeedId(Long feedId, Pageable pageable);
}
