package com.serch.server.domains.nearby.models.go.activity;

import com.serch.server.annotations.CoreID;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import com.serch.server.domains.nearby.models.go.GoLocation;
import com.serch.server.domains.nearby.models.go.GoMedia;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.enums.trip.TripStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_activities")
public class GoActivity extends BaseDateTime {
    @Id
    @CoreID(name = "go_activity_generator", prefix = "ge", end = 10, replaceSymbols = true)
    @GeneratedValue(generator = "go_activity_gen")
    @Column(name = "id", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String id;

    @NotBlank(message = "Event name cannot be empty or blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TripStatus status = TripStatus.WAITING;

    @OneToOne(mappedBy = "activity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GoLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "interest_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_interest_id_fkey")
    )
    private GoInterest interest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_activity_user_id_fkey")
    )
    private GoUser user;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<GoAttendingUser> attendingUsers;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<GoMedia> images;

    public boolean isEnded() {
        return getStatus() == TripStatus.CLOSED;
    }

    @Override
    public String toString() {
        return "GoEvent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", location=" + location +
                ", interest=" + interest +
                ", user=" + user +
                ", attendingUsers=" + attendingUsers +
                ", images=" + images +
                '}';
    }
}