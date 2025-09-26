package com.starterpack.pack.repository;

import com.starterpack.member.entity.Member;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.entity.PackLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PackLikeRepository extends JpaRepository<PackLike, Long> {
    boolean existsByPackAndMember(Pack pack, Member member);
    void deleteByPackAndMember(Pack pack, Member member);

    @Query("SELECT pl FROM PackLike pl JOIN FETCH  pl.member WHERE pl.pack = :pack")
    Page<PackLike> findByPack(@Param("pack") Pack pack, Pageable pageable);
}
