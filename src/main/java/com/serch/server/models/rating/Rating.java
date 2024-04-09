package com.serch.server.models.rating;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.call.Call;
import com.serch.server.models.company.Product;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "platform", name = "ratings")
public class Rating extends BaseModel {
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "rating", nullable = false)
    private Double rating = 5.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "trip_id",
            foreignKey = @ForeignKey(name = "trip_id_fkey")
    )
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "call_channel",
            referencedColumnName = "channel",
            foreignKey = @ForeignKey(name = "call_channel_fkey")
    )
    private Call call;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "product_id_fkey")
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "guest_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "guest_id_fkey")
    )
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rated_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "user_rated_id")
    )
    private Profile rated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rater_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "user_rater_id")
    )
    private Profile rater;
}
