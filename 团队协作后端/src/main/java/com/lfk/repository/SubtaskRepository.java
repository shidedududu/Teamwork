package com.lfk.repository;

import com.lfk.model.SubtaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtaskRepository extends JpaRepository<SubtaskEntity,Integer> {
}
