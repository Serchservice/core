package com.serch.server.models.trip;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseEntity;
import com.serch.server.enums.chat.MessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "pricing", name = "haggle_messages")
public class PriceChat extends BaseEntity {
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Message cannot be empty or null")
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "MessageStatus must be an enum")
    private MessageStatus status = MessageStatus.SENT;

    @Column(nullable = false)
    private String sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "haggle_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "haggle_id_fkey")
    )
    private PriceHaggle haggle;
}
