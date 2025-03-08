package com.serch.server.domains.nearby.models.go.user;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.GoPaymentAuthorization;
import com.serch.server.domains.nearby.models.go.addon.GoAddonPlan;
import com.serch.server.enums.nearby.GoUserAddonStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_user_addons")
public class GoUserAddon extends BaseModel {
    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "subscription_date", nullable = false)
    private LocalDate subscriptionDate;

    @Column(name = "is_recurring", updatable = false)
    private Boolean isRecurring = true;

    @Column(nullable = false)
    private Integer trials = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoUserAddonStatus status =  GoUserAddonStatus.ACTIVE;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "addon_plan_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_user_addon_plan_id_fkey")
    )
    private GoAddonPlan plan;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_user_addon_user_id_fkey")
    )
    private GoUser user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "addon", orphanRemoval = true)
    private GoUserAddonChange change;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "addon", orphanRemoval = true)
    private GoPaymentAuthorization authorization;

    public boolean isActive() {
        return getStatus() == GoUserAddonStatus.ACTIVE || getStatus() == GoUserAddonStatus.RENEWAL_DUE;
    }

    public boolean isDeactivated() {
        return getStatus() == GoUserAddonStatus.CANCELLED && getNextBillingDate().isAfter(LocalDate.now());
    }

    public boolean hasMultiple() {
        return getPlan().getAddon().getPlans() != null && !getPlan().getAddon().getPlans().isEmpty();
    }

    public boolean isExpired() {
        return getStatus() == GoUserAddonStatus.EXPIRED;
    }

    public boolean hasAuthorization() {
        return getAuthorization() != null;
    }

    public boolean hasSwitch() {
        return getChange() != null;
    }
}