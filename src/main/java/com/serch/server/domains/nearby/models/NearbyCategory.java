package com.serch.server.domains.nearby.models;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories", schema = "nearby")
public class NearbyCategory extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String type;

    @Column(nullable = false)
    private Long count = 0L;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<NearbyTimeline> timelines;

    public void increment() {
        this.setCount(this.getCount() + 1);
    }
}