package com.starterpack.member.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
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

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

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

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Member() {}

    public Member(String email, String password, String name, String nickname, Provider provider, String providerId, LocalDate birthDate, Gender gender, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.isActive = true;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    // 일반 사용자 생성
    public static Member createUser(String email, String password, String name, String nickname, Provider provider, String providerId, LocalDate birthDate, Gender gender, String phoneNumber) {
        Member member = new Member(email, password, name, nickname, provider, providerId, birthDate, gender, phoneNumber);
        member.setRole(Role.USER);
        member.setProfileImageUrl("https://cdn-icons-png.flaticon.com/512/12225/12225935.png");
        return member;
    }

    // 관리자 생성
    public static Member createAdmin(String email, String password, String name, String nickname, Provider provider, String providerId, LocalDate birthDate, Gender gender, String phoneNumber) {
        Member member = new Member(email, password, name, nickname, provider, providerId,  birthDate, gender, phoneNumber);
        member.setRole(Role.ADMIN);
        return member;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 닉네임 설정 메서드
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Provider 열거형
    public enum Provider {
        EMAIL, KAKAO
    }
}

