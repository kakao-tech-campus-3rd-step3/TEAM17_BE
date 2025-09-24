package com.starterpack.pack.entity;

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
@Table(name = "pack_like", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_pack_like_member",
                columnNames = {"pack_id", "member_id"}
        )
})
@Getter
public class PackLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pack_id", nullable = false)
    private Pack pack;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected PackLike() {}

    public PackLike(Pack pack, Member member) {
        this.pack = pack;
        this.member = member;
    }
}
