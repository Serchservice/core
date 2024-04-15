package com.serch.server.models.conversation;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.generators.chat.ChatRoomID;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * The ChatRoom class represents a chat room entity in the system.
 * It stores information about chat rooms, including the state and the associated messages, creator, and roommate.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link Enumerated}</li>
 *     <li>{@link OneToMany}</li>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link ChatMessage} - The chat messages associated with the chat room.</li>
 *     <li>{@link Profile} - The profiles of the creator and roommate associated with the chat room.</li>
 * </ul>
 * @see BaseDateTime
 * @see SerchEnum
 * @see MessageState
 */
@Getter
@Setter
@Entity(name = "chat_rooms")
@Table(schema = "conversation", name = "chat_rooms")
public class ChatRoom extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "room_id_gen", type = ChatRoomID.class)
    @GeneratedValue(generator = "room_id_gen")
    private String id;

    @Column(name = "state", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "MessageState must be an enum")
    private MessageState state = MessageState.ACTIVE;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "creator",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "creator_id_fkey")
    )
    private Profile creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "roommate",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "roommate_id_fkey")
    )
    private Profile roommate;
}
