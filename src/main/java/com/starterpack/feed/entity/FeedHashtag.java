package com.starterpack.feed.entity;

import com.starterpack.hashtag.entity.Hashtag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_hashtag", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"feed_id", "hashtag_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    @Column(name = "tag_order", nullable = false)
    private Integer tagOrder;

    public FeedHashtag(Feed feed, Hashtag hashtag, Integer tagOrder) {
        this.feed = feed;
        this.hashtag = hashtag;
        this.tagOrder = tagOrder;
    }

    public void updateOrder(List<Hashtag> newHashtags) {
        int newOrder = newHashtags.indexOf(hashtag);

        if (newOrder != -1) {
            this.setTagOrder(newOrder);
        }
    }

    private void setTagOrder(int tagOrder) {
        this.tagOrder = tagOrder;
    }
}
