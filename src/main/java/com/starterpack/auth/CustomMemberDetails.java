package com.starterpack.auth;

import com.starterpack.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security가 사용자를 인증할 때 사용하는 표준 래퍼 클래스
 * 순수한 Member 객체를 감싸서, Security가 요구하는 UserDetails를 제공
 */
@Getter
@Setter
public class CustomMemberDetails implements UserDetails {
    private final Member member;

    public CustomMemberDetails(Member member) {
        this.member = member;
    }

    /**
     * 사용자의 권한(Role) 목록을 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole().getKey()));
    }

    /**
     * 사용자의 암호화된 비밀번호 반환
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 사용자의 email 반환
     */
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    /**
     * 계정 활성화 여부 반환
     */
    @Override
    public boolean isEnabled() {
        return member.getIsActive();
    }

    /**
     * 계정 만료 여부 반환
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    /**
     * 계정 잠금 여부 반환
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부 반환
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
