package com.serch.server.models.conversation;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.generators.CallID;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

/**
 * The Call class represents a call entity in the system.
 * It stores information about calls, including the channel, duration, session count, type, status, amount spent, and associated profiles.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Profile} - The profiles associated with the call.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link Call#checkIfActive()} - Checks if the call is active.</li>
 * </ul>
 * @see BaseDateTime
 * @see SerchEnum
 * @see Profile
 */
@Getter
@Setter
@Entity(name = "calls")
@Table(schema = "conversation", name = "calls")
public class Call extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT", updatable = false)
    @GenericGenerator(name = "call_gen", type = CallID.class)
    @GeneratedValue(generator = "call_gen")
    private String channel;

    @Column(name = "duration", columnDefinition = "TEXT", nullable = false)
    private String duration = "00:00:00";

    @Column(nullable = false)
    private Integer session = 0;

    @Column(name = "retries", nullable = false)
    private Integer retries = 0;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "CallType must be an enum")
    private CallType type;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "CallStatus must be an enum")
    private CallStatus status = CallStatus.CALLING;

    @Column(name = "amount_spent", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "called_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "called_id_fkey")
    )
    private Profile called;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "caller_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "caller_id_fkey")
    )
    private Profile caller;

    /**
     * Checks if the call is active.
     * @throws CallException if the call status is CLOSED, DECLINED, or MISSED.
     */
    public void checkIfActive() {
        if(getStatus() == CallStatus.CLOSED || getStatus() == CallStatus.DECLINED || getStatus() == CallStatus.MISSED) {
            throw new CallException("Call is %s".formatted(getStatus().getType()));
        }
    }

    public boolean isVoice() {
        return type == CallType.VOICE;
    }
}
