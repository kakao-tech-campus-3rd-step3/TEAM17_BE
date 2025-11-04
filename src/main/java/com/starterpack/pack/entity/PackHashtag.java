package com.starterpack.pack.entity;

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
@Table(name = "pack_hashtag", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pack_id", "hashtag_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PackHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = false)
    private Pack pack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    @Column(name = "tag_order", nullable = false)
    private Integer tagOrder;

    public PackHashtag(Pack pack, Hashtag hashtag, Integer tagOrder) {
        this.pack = pack;
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
