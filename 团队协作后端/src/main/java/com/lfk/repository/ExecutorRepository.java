package com.lfk.repository;

import com.lfk.model.ExecutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExecutorRepository extends JpaRepository<ExecutorEntity,Integer> {

    @Query("select e from ExecutorEntity e where e.taskByTaskId.id=?1")
    List<ExecutorEntity> findByTaskByTaskId(int taskId);

    @Query("select e from ExecutorEntity e where e.taskByTaskId.id=?1 and e.userByUserId.id=?2")
    ExecutorEntity findByTaskByTaskIdAndUserByUserId(int taskId,int userId);

}
