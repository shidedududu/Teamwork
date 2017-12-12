package com.lfk.repository;

import com.lfk.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {


    @Query("select user from UserEntity user where user.userName=?1")
    UserEntity findByUserName(String userName);


}