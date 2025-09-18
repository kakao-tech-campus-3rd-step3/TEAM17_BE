package com.starterpack.feed.repository;

import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedLike;
import com.starterpack.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    boolean existsByFeedAndMember(Feed feed, Member member);
    void deleteByFeedAndMember(Feed feed, Member member);

    @Query("SELECT fl FROM FeedLike fl JOIN FETCH fl.member WHERE fl.feed = :feed")
    Page<FeedLike> findByFeed(@Param("feed") Feed feed, Pageable pageable);
}
