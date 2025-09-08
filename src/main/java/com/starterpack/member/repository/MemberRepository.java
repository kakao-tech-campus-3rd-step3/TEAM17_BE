package com.starterpack.member.repository;

import com.starterpack.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    
    // 이메일로 멤버 조회
    Optional<Member> findByEmail(String email);
    
    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);
    
    // 프로바이더와 프로바이더 ID로 멤버 조회 (소셜 로그인용)
    Optional<Member> findByProviderAndProviderId(Member.Provider provider, String providerId);
    
    // 활성화된 멤버만 조회
    Optional<Member> findByEmailAndIsActiveTrue(String email);
    
    // 활성화된 멤버만 프로바이더와 프로바이더 ID로 조회
    Optional<Member> findByProviderAndProviderIdAndIsActiveTrue(Member.Provider provider, String providerId);
}
