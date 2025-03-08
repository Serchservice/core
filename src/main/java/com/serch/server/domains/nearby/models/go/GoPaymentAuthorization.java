package com.serch.server.domains.nearby.models.go;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_payment_authorizations")
public class GoPaymentAuthorization extends BaseModel {
    @Column(columnDefinition = "TEXT", name = "authorization_code")
    private String authorizationCode;

    @Column(columnDefinition = "TEXT", name = "card_type")
    private String cardType;

    @Column(columnDefinition = "TEXT", name = "exp_month")
    private String expMonth;

    @Column(columnDefinition = "TEXT", name = "exp_year")
    private String expYear;

    @Column(columnDefinition = "TEXT", name = "country_code")
    private String countryCode;

    @Column(columnDefinition = "TEXT", name = "account_name")
    private String accountName;

    @Column(columnDefinition = "TEXT")
    private String last4;

    @Column(columnDefinition = "TEXT")
    private String bin;

    @Column(columnDefinition = "TEXT")
    private String bank;

    @Column(columnDefinition = "TEXT")
    private String channel;

    @Column(columnDefinition = "TEXT")
    private String signature;

    private Boolean reusable;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_addon_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_payment_user_addon_id_fkey")
    )
    private GoUserAddon addon;
}