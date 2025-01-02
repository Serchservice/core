package com.serch.server.models.conversation;

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

import java.util.UUID;

/**
 * The ChatMessage class represents a chat message entity in the system.
 * It stores information about chat messages, including the message content, status, type, state, duration, file size, file caption,
 * navigate, whether it's a search message, and the associated chat room, sender, receiver, and replied message.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link ChatRoom} - The chat room associated with the chat message.</li>
 *     <li>{@link Profile} - The profiles of the sender and receiver associated with the chat message.</li>
 *     <li>{@link ChatMessage} - The replied message associated with the chat message.</li>
 * </ul>
 * @see BaseDateTime
 * @see SerchEnum
 * @see MessageType
 * @see MessageStatus
 * @see MessageState
 */
@Getter
@Setter
@Entity(name = "chat_messages")
@Table(schema = "conversation", name = "chat_messages")
public class ChatMessage extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT", unique = true, updatable = false)
    @GenericGenerator(name = "message_id_gen", type = MessageID.class)
    @GeneratedValue(generator = "message_id_gen")
    private String id;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Message cannot be empty or null")
    private String message;

    @Column(name = "sender_message", columnDefinition = "TEXT DEFAULT ''", nullable = false)
    @NotBlank(message = "Sender Message cannot be empty or null")
    private String senderMessage;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "MessageStatus must be an enum")
    private MessageStatus status = MessageStatus.SENT;

    @Column(name = "type", nullable = false, updatable = false)
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

    @Column(name = "sender", nullable = false, updatable = false)
    private UUID sender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "replied_message_id",
            referencedColumnName = "id",
            updatable = false,
            foreignKey = @ForeignKey(name = "replied_message_id_fkey")
    )
    private ChatMessage replied = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "room_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "room_id_fkey")
    )
    private ChatRoom chatRoom;
}