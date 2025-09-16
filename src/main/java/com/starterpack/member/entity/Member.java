package com.starterpack.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Provider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Member() {}

    public Member(String email, String password, String name, Provider provider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.isActive = true;
    }

    // 일반 사용자 생성
    public static Member createUser(String email, String password, String name, Provider provider, String providerId) {
        Member member = new Member(email, password, name, provider, providerId);
        member.setRole(Role.USER);
        return member;
    }

    // 관리자 생성
    public static Member createAdmin(String email, String password, String name, Provider provider, String providerId) {
        Member member = new Member(email, password, name, provider, providerId);
        member.setRole(Role.ADMIN);
        return member;
    }

    // Provider 열거형
    public enum Provider {
        EMAIL, KAKAO
    }
}

