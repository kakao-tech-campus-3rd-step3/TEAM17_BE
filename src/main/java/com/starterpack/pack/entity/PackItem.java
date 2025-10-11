package com.starterpack.pack.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pack_item")
public class PackItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = false)
    private Pack pack;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String linkUrl;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Builder
    public PackItem(Pack pack, String name, String linkUrl, String description, String imageUrl) {
        this.pack = pack;
        this.name = name;
        this.linkUrl = linkUrl;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public void update(String name, String linkUrl, String description, String imageUrl) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (linkUrl != null) {
            this.linkUrl = linkUrl;
        }
        if (description != null) {
            this.description = description;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}
