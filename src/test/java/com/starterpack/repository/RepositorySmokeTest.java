package com.starterpack.repository;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.repository.PackRepository;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositorySmokeTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PackRepository packRepository;

    @Test
    void category_product_pack_CRUD_and_relations() {
        // 카테고리 저장
        Category cat = new Category();
        cat.setName("스포츠");
        cat.setSrc("sports.png");
        cat = categoryRepository.save(cat);

        // 상품 저장
        Product p1 = new Product();
        p1.setName("래쉬가드");
        p1.setProductType("TOP");
        p1.setCost(45000);
        p1.setLikeCount(7);
        p1.setCategory(cat);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("무릎보호대");
        p2.setProductType("GEAR");
        p2.setCost(12000);
        p2.setLikeCount(2);
        p2.setCategory(cat);
        productRepository.save(p2);

        // 팩 저장 + 상품 연관
        Pack pack = new Pack();
        pack.setName("주짓수 스타터팩");
        pack.setCategory(cat);
        pack.setTotalCost(57000);
        pack.setPackLikeCount(3);
        pack.setSrc("pack.png");
        pack.setDescription("주짓수 스타터팩");

        // 편의 메서드 사용
        pack.addProduct(p1);
        pack.addProduct(p2);
        pack = packRepository.save(pack);

        // 검증 1: 카테고리로 상품 찾기
        List<Product> byCat = productRepository.findByCategory_Id(cat.getId());
        assertThat(byCat).hasSize(2);

        // 검증 2: 이름 부분검색
        assertThat(productRepository.findByNameContainingIgnoreCase("래쉬")).hasSize(1);

        // 검증 3: 팩-상품 연관
        Pack saved = packRepository.findById(pack.getId()).orElseThrow();
        assertThat(saved.getProducts()).extracting(Product::getName)
                .containsExactlyInAnyOrder("래쉬가드","무릎보호대");
    }
}
