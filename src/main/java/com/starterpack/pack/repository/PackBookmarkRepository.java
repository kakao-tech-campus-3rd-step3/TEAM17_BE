package com.starterpack.pack.repository;

import com.starterpack.member.entity.Member;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackBookmark;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PackBookmarkRepository extends JpaRepository<PackBookmark, Long> {
    boolean existsByPackAndMember(Pack pack, Member member);
    int deleteByPackAndMember(Pack pack, Member member);

    @Query("SELECT pb.pack.id FROM PackBookmark pb WHERE pb.member = :member AND pb.pack.id IN :packIds")
    Set<Long> findPackIdsByMemberAndPackIds(@Param("member") Member member, @Param("packIds") List<Long> packIds);

    Page<PackBookmark> findByMemberOrderByCreatedAtDesc(@Param("member") Member member, Pageable pageable);
}
