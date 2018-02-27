package com.lfk.repository;

import com.lfk.model.SubtaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubtaskRepository extends JpaRepository<SubtaskEntity,Integer> {
    @Modifying
    @Transactional
    @Query("update SubtaskEntity s set s.isFinished=?1 where s.id=?2")
    void setFinished(short finished,int id);

    @Query("select s from SubtaskEntity s where s.taskByTaskId.id=?1")
    List<SubtaskEntity> getSubtaskEntityByTaskId(int taskId);
}
