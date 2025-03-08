package com.serch.server.domains.nearby.models.go;

import com.serch.server.bases.BaseLocation;
import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_locations")
public class GoLocation extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT default ''")
    private String country = "";

    @Column(nullable = false, columnDefinition = "TEXT default ''")
    private String state = "";

    @Column(nullable = false, columnDefinition = "TEXT default ''")
    private String city = "";

    @NotBlank(message = "Location address cannot be empty or blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String place;

    @Column(nullable = false, columnDefinition = "TEXT default ''")
    private String localGovernmentArea = "";

    @Column(nullable = false, columnDefinition = "TEXT default ''")
    private String streetNumber = "";

    @Column(nullable = false, columnDefinition = "TEXT default ''")
    private String streetName = "";

    @Column(nullable = false)
    private Double latitude = 0.0;

    @Column(nullable = false)
    private Double longitude = 0.0;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_location_user_id_fkey")
    )
    private GoUser user;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "activity_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_location_activity_id_fkey")
    )
    private GoActivity activity;

    public BaseLocation getBase() {
        BaseLocation location = new BaseLocation();
        location.setLatitude(getLatitude());
        location.setLongitude(getLongitude());
        location.setPlace(getPlace());

        return location;
    }
}