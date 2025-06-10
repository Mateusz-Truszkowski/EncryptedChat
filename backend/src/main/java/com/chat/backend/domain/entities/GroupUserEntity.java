package com.chat.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "group_user")
public class GroupUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_user_id_seq")
    private Integer id;
    @ManyToOne(cascade = CascadeType.MERGE)
    private UserEntity user;
    @ManyToOne(cascade = CascadeType.ALL)
    private GroupEntity group;
}
