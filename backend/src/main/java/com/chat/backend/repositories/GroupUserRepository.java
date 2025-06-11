package com.chat.backend.repositories;

import com.chat.backend.domain.entities.GroupUserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupUserRepository extends CrudRepository<GroupUserEntity, Integer> {
    @Query("SELECT gu FROM GroupUserEntity gu WHERE gu.user.id = :userId AND gu.group.id = :groupId")
    Optional<GroupUserEntity> findAllByUserAndGroupId(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    void deleteByGroupId(Integer groupId);

    void deleteByUserId(Integer id);
}
