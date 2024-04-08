package com.serch.server.models.chat;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.enums.chat.MessageType;
import com.serch.server.generators.chat.MessageID;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity(name = "chat_messages")
@Table(schema = "conversation", name = "chat_messages")
public class ChatMessage extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "message_id_gen", type = MessageID.class)
    @GeneratedValue(generator = "message_id_gen")
    private String id;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Message cannot be empty or null")
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "MessageStatus must be an enum")
    private MessageStatus status = MessageStatus.SENT;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "MessageType must be an enum")
    private MessageType type = MessageType.TEXT;

    @Column(name = "state", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "MessageState must be an enum")
    private MessageState state = MessageState.ACTIVE;

    @Column(name = "file_duration", columnDefinition = "TEXT")
    private String duration = null;

    @Column(name = "file_size", columnDefinition = "TEXT")
    private String fileSize = null;

    @Column(name = "file_caption", columnDefinition = "TEXT")
    private String fileCaption = null;

    @Column(name = "navigate", columnDefinition = "TEXT")
    private String navigate = null;

    @Column(name = "is_serch", nullable = false)
    private Boolean isSerch = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "replied_message_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "replied_message_id_fkey")
    )
    private ChatMessage replied = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "room_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "room_id_fkey")
    )
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sender_id_fkey")
    )
    private Profile sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "receiver_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "receiver_id_fkey")
    )
    private Profile receiver;
}
