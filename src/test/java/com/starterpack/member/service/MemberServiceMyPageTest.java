package com.starterpack.member.service;

import com.starterpack.member.dto.MyPageResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceMyPageTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 비로그인 사용자가 마이페이지를 조회할 때 isMe가 false인지 확인
     * - 닉네임, 취미, 한 줄 소개 필드가 정확히 매핑되는지 확인
     */
    @Test
    void testMyPageForAnonymousUser_ReturnsIsMeFalse() {
        // given
        Member member = Member.createUser(
                "test@test.com",
                "password",
                "Test Name",
                "testnick",
                Member.Provider.EMAIL,
                "providerId",
                null,
                null,
                null
        );
        member.setHobby("독서");
        member.setBio("한 줄 소개");
        member = memberRepository.save(member);

        // when - 비로그인 사용자(null)가 마이페이지 조회
        MyPageResponseDto response = memberService.getMyPage(member.getUserId(), null);

        // then - isMe가 false이고 모든 필드가 정확히 매핑됨
        assertThat(response).isNotNull();
        assertThat(response.nickname()).isEqualTo("testnick");
        assertThat(response.hobby()).isEqualTo("독서");
        assertThat(response.bio()).isEqualTo("한 줄 소개");
        assertThat(response.isMe()).isFalse();
    }

    /**
     * 본인(userId == currentUserId)이 마이페이지를 조회할 때 isMe가 true인지 확인
     */
    @Test
    void testMyPageForOwner_ReturnsIsMeTrue() {
        // given
        Member member = Member.createUser(
                "test2@test.com",
                "password",
                "Test Name 2",
                "testnick2",
                Member.Provider.EMAIL,
                "providerId2",
                null,
                null,
                null
        );
        member = memberRepository.save(member);

        // when - 본인(member.getUserId())이 자신의 마이페이지 조회
        MyPageResponseDto response = memberService.getMyPage(member.getUserId(), member.getUserId());

        // then - isMe가 true임
        assertThat(response.isMe()).isTrue();
    }
}

