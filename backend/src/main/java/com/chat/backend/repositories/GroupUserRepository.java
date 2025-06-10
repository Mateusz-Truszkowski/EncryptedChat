package com.chat.backend.repositories;

import com.chat.backend.domain.entities.GroupUserEntity;
import org.springframework.data.repository.CrudRepository;

public interface GroupUserRepository extends CrudRepository<GroupUserEntity, Integer> {
}
