package com.starterpack.feed.entity;

import com.starterpack.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="feed_like", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_feed_like_member",
                columnNames = {"feed_id", "member_id"}
        )
})
@Getter
public class FeedLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="feed_id", nullable=false)
    private Feed feed;

    @ManyToOne
    @JoinColumn(name="member_id", nullable=false)
    private Member member;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    protected FeedLike() {}

    public FeedLike(Feed feed, Member member) {
        this.feed = feed;
        this.member = member;
    }
}
