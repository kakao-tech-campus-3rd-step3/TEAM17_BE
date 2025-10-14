package com.starterpack.repository;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Role;
import com.starterpack.member.repository.MemberRepository;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackItem;
import com.starterpack.pack.repository.PackRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositorySmokeTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PackRepository packRepository;

    @Test
    void category_product_pack_CRUD_and_relations() {
        // 카테고리 저장
        Category cat = new Category();
        cat.setName("스포츠");
        cat.setSrc("sports.png");
        cat = categoryRepository.save(cat);

        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword("password123");
        member.setName("테스터");
        member.setNickname("테스트유저");
        member.setRole(Role.USER);
        member.setIsActive(true);
        member = memberRepository.save(member);

        Pack pack = Pack.builder()
                .name("주짓수 스타터팩")
                .category(cat)
                .member(member)
                .price(57000)
                .mainImageUrl("pack.png")
                .description("주짓수를 시작하는데 필요한 모든 것!")
                .build();

        // ✅ 5. PackItem 추가 (새로운 구조)
        PackItem item1 = PackItem.builder()
                .pack(pack)
                .name("래쉬가드")
                .linkUrl("https://example.com/rashguard")
                .description("고품질 래쉬가드입니다.")
                .imageUrl("rashguard.jpg")
                .build();

        PackItem item2 = PackItem.builder()
                .pack(pack)
                .name("무릎보호대")
                .linkUrl("https://example.com/knee-pad")
                .description("무릎을 보호해주는 필수 장비입니다.")
                .imageUrl("knee-pad.jpg")
                .build();

        pack.addItem(item1);
        pack.addItem(item2);

        pack = packRepository.save(pack);

        Pack saved = packRepository.findById(pack.getId()).orElseThrow();
        assertThat(saved.getItems()).hasSize(2);
        assertThat(saved.getItems())
                .extracting(PackItem::getName)
                .containsExactlyInAnyOrder("래쉬가드", "무릎보호대");

        assertThat(saved.getName()).isEqualTo("주짓수 스타터팩");
        assertThat(saved.getPrice()).isEqualTo(57000);
        assertThat(saved.getMember().getNickname()).isEqualTo("테스트유저");
        assertThat(saved.getCategory().getName()).isEqualTo("스포츠");
    }
}
