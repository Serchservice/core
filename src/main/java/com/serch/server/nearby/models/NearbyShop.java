package com.serch.server.nearby.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shops", schema = "nearby")
public class NearbyShop extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String provider;

    @Column(nullable = false)
    private Long count = 0L;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<NearbyTimeline> timelines;

    public void increment() {
        this.setCount(this.getCount() + 1);
    }
}