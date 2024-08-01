package com.serch.server.models.trip;

import com.serch.server.bases.BaseTrip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "invites")
public class TripInvite extends BaseTrip {
    private UUID selected;

    @OneToMany(mappedBy = "invite", cascade = CascadeType.ALL)
    private List<TripInviteQuotation> quotes;

    @OneToMany(mappedBy = "invite", cascade = CascadeType.ALL)
    private List<ShoppingItem> shoppingItems;

    @OneToOne(mappedBy = "invite", cascade = CascadeType.ALL)
    private ShoppingLocation shoppingLocation;
}