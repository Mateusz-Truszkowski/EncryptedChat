package com.chat.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id")
    private GroupEntity group;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sender_id")
    private UserEntity sender;
    private String content;
    private LocalDateTime sentAt;
    private String attachment;
    private String status;
}
