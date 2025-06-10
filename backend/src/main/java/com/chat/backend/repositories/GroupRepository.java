package com.chat.backend.repositories;

import com.chat.backend.domain.entities.GroupEntity;
import com.chat.backend.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends CrudRepository<GroupEntity, Integer> {
    @Query("SELECT g FROM GroupEntity g, GroupUserEntity gu JOIN gu.group WHERE g = gu.group AND gu.user = :user")
    List<GroupEntity> findByUser(@Param("user") UserEntity user);
}
