package com.starterpack.feed.specification;

import com.starterpack.feed.entity.Feed;
import com.starterpack.member.entity.Member;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class FeedSpecification {
    public static Specification<Feed> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Feed, Member> userJoin = root.join("user", JoinType.LEFT);
            String pattern = "%" + keyword.toUpperCase() + "%";

            return criteriaBuilder.like(
                            criteriaBuilder.upper(userJoin.get("email")), pattern);
        };
    }

    public static Specification<Feed> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

}
