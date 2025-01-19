package com.serch.server.bases;

import com.serch.server.annotations.CoreID;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseTrip extends BaseDateTime {
    @Id
    @CoreID(name = "trip_generator", prefix = "STRIP")
    @GeneratedValue(generator = "trip_gen")
    @Column(name = "id", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String id;

    @Column(nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private TripMode mode;

    @Column(name = "account", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String account;

    @Column(name = "link_id", columnDefinition = "TEXT", updatable = false)
    private String linkId;

    @Column(name = "skill", columnDefinition = "TEXT")
    private String skill;

    @Column(name = "car", columnDefinition = "TEXT")
    private String car;

    @Column(name = "category", nullable = false, columnDefinition = "TEXT", updatable = false)
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    @Column(name = "audio", columnDefinition = "TEXT")
    private String audio;

    @Column(name = "problem", columnDefinition = "TEXT")
    private String problem;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "place_id", columnDefinition = "TEXT")
    private String placeId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;
}