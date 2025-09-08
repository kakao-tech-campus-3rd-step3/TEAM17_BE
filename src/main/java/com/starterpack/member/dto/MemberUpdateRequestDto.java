package com.starterpack.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class MemberUpdateRequestDto {

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private String name;

    private String profileImageUrl;

    // 기본 생성자
    public MemberUpdateRequestDto() {}

    // 생성자
    public MemberUpdateRequestDto(String email, String password, String name, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    // --- getters/setters ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
