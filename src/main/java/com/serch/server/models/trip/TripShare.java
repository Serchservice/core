package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.trip.TripShareOption;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "sharings")
public class TripShare extends BaseModel {
    @Column(name = "first_name", columnDefinition = "TEXT")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "TEXT")
    private String lastName;

    @Column(name = "phone_number", columnDefinition = "TEXT")
    private String phoneNumber;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TripShareOption option = TripShareOption.ONLINE;

    @Column(nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private TripStatus status;

    @OneToMany(mappedBy = "sharing", cascade = CascadeType.ALL)
    private List<TripTimeline> timelines;

    @OneToOne(mappedBy = "sharing", cascade = CascadeType.ALL)
    private TripAuthentication authentication;

    @OneToOne(mappedBy = "sharing", cascade = CascadeType.ALL)
    private MapView mapView;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "trip_provider_id_fkey")
    )
    private Profile provider;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_sharing_id_fkey")
    )
    private Trip trip;

    public String fullName() {
        return firstName + " " + lastName;
    }

    public boolean isOffline() {
        return getOption() == TripShareOption.OFFLINE;
    }
}
