package com.serch.server.models.schedule;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.generators.ScheduleID;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(schema = "platform", name = "schedules")
public class Schedule extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "schedule_seq", type = ScheduleID.class)
    @GeneratedValue(generator = "schedule_seq")
    private String id;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ScheduleStatus must be an enum")
    private ScheduleStatus scheduleStatus;

    @Column(name = "time", nullable = false, columnDefinition = "TEXT")
    private String scheduleTime;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String reason = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "provider_id_fkey")
    )
    private Profile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private Profile user;
}
