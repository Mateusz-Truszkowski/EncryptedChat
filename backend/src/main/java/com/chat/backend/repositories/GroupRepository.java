package com.chat.backend.repositories;

import com.chat.backend.domain.entities.GroupEntity;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<GroupEntity, Integer> {
}
