package com.starterpack.feed.repository;

import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedBookmark;
import com.starterpack.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedBookmarkRepository extends JpaRepository<FeedBookmark, Long> {
    boolean existsByFeedAndMember(Feed feed, Member member);
    void deleteByFeedAndMember(Feed feed, Member member);
}
