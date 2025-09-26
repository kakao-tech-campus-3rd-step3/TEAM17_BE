package com.starterpack.pack.repository;

import com.starterpack.member.entity.Member;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackBookmarkRepository extends JpaRepository<PackBookmark, Long> {
    boolean existsByPackAndMember(Pack pack, Member member);
    void deleteByPackAndMember(Pack pack, Member member);
}
