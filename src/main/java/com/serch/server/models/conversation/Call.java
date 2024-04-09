package com.serch.server.models.conversation;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.generators.CallID;
import com.serch.server.models.account.Profile;
import com.serch.server.models.rating.Rating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Getter
@Setter
@Entity(name = "calls")
@Table(schema = "conversation", name = "calls")
public class Call extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "call_gen", type = CallID.class)
    @GeneratedValue(generator = "call_gen")
    private String channel;

    @Column(name = "duration", columnDefinition = "TEXT")
    private String duration = null;

    @Column(name = "session_count", nullable = false)
    private Integer sessionCount = 0;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "CallType must be an enum")
    private CallType type;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "CallStatus must be an enum")
    private CallStatus status = CallStatus.CALLING;

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

    @OneToMany(mappedBy = "call", fetch = FetchType.LAZY)
    private List<Rating> ratings;
}
