package com.starterpack.pack.repository;

import com.starterpack.member.entity.Member;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackLike;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PackLikeRepository extends JpaRepository<PackLike, Long> {
    boolean existsByPackAndMember(Pack pack, Member member);
    int deleteByPackAndMember(Pack pack, Member member);

    @Query("SELECT pl FROM PackLike pl JOIN FETCH  pl.member WHERE pl.pack = :pack")
    Page<PackLike> findByPack(@Param("pack") Pack pack, Pageable pageable);

    @Query("SELECT pl.pack.id FROM PackLike pl WHERE pl.member = :member AND pl.pack.id IN :packIds")
    Set<Long> findPackIdsByMemberAndPackIds(@Param("member") Member member, @Param("packIds") List<Long> packIds);
}
