package com.lfk.repository;

import com.lfk.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {

    @Query("select t from TaskEntity t where t.projectByProId.id=?1")
    List<TaskEntity> findByProId(int proId);

    @Modifying
    @Transactional
    @Query("update TaskEntity t set t.completion=?1 where t.id=?2")
    void updateCompletion(double complrtion,int id);
}
