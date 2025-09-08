package com.starterpack.member.dto;

import com.starterpack.member.entity.Member;

import java.time.LocalDateTime;

public class MemberResponseDto {

    private Long userId;
    private String email;
    private String name;
    private Member.Provider provider;
    private String providerId;
    private String profileImageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public MemberResponseDto() {}

    // 생성자
    public MemberResponseDto(Member member) {
        this.userId = member.getUserId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.provider = member.getProvider();
        this.providerId = member.getProviderId();
        this.profileImageUrl = member.getProfileImageUrl();
        this.isActive = member.getIsActive();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }

    // --- getters/setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Member.Provider getProvider() { return provider; }
    public void setProvider(Member.Provider provider) { this.provider = provider; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
