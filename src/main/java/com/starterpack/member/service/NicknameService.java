package com.starterpack.member.service;

import com.starterpack.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final MemberRepository memberRepository;
    
    // 별명 종류 리스트 - 언제든 수정 가능
    private static final List<String> NICKNAME_PREFIXES = List.of(
        "제빵사", "요리사", "디자이너", "개발자", "작가", "화가", "음악가", "운동선수",
        "탐험가", "이상현", "기사", "궁수", "마법사", "상인", "농부", "의사",
        "선생님", "학생", "연구원", "예술가", "사진작가", "여행자", "요가사",
        "요리연구가", "카페사장", "북리뷰어", "영화감상가", "게이머", "독서가"
    );

    // 중복되지 않는 랜덤 닉네임 생성
    public String generateUniqueNickname() {
        String nickname;
        int maxAttempts = 100; // 무한루프 방지
        int attempts = 0;
        
        do {
            nickname = generateRandomNickname();
            attempts++;
            
            if (attempts >= maxAttempts) {
                // 최대 시도 횟수 초과 시 타임스탬프 추가
                nickname = generateRandomNickname() + "_" + System.currentTimeMillis();
                break;
            }
        } while (memberRepository.existsByNickname(nickname));
        
        return nickname;
    }

    // 랜덤 닉네임 생성 (별명 + 랜덤번호)
    private String generateRandomNickname() {
        Random random = new Random();
        String prefix = NICKNAME_PREFIXES.get(random.nextInt(NICKNAME_PREFIXES.size()));
        int randomNumber = random.nextInt(10000000); // 0~9999999 (7자리)
        return prefix + randomNumber;
    }

    // 닉네임 중복 확인
    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
