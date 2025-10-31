package com.starterpack.feed.repository;

import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedBookmark;
import com.starterpack.member.entity.Member;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedBookmarkRepository extends JpaRepository<FeedBookmark, Long> {
    boolean existsByFeedAndMember(Feed feed, Member member);
    int deleteByFeedAndMember(Feed feed, Member member);

    @Query("SELECT fb.feed.id FROM FeedBookmark fb WHERE fb.member = :member AND fb.feed.id IN :feedIds")
    Set<Long> findFeedIdsByMemberAndFeedIds(@Param("member")Member member, @Param("feedIds") List<Long> feedIds);
}
