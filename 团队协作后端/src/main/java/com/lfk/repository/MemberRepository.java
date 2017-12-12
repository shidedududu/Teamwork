package com.lfk.repository;

import com.lfk.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity,Integer> {

    @Query("select m from MemberEntity m where m.userByUserId.id=?1")
    List<MemberEntity> findByUserId(int userId);

    @Query("select m from MemberEntity m where m.userByUserId.id=?1 and m.projectByProId.id=?2")
    MemberEntity findByUserIdAndProId(int userId,int projectId);
}
