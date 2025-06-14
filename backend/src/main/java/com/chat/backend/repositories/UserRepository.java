package com.chat.backend.repositories;

import com.chat.backend.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findUserByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.role LIKE %:role%")
    boolean existsByRolesContaining(@Param("role") String role);

    boolean existsByUsername(String username);

    @Query("SELECT gu.user FROM GroupUserEntity gu WHERE gu.group.id = :groupId")
    List<UserEntity> findAllUsersByGroupId(@Param("groupId") Integer groupId);
}
