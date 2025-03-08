package com.serch.server.domains.nearby.models.go.addon;

import com.serch.server.annotations.CoreID;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.nearby.GoAddonPlanInterval;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_addon_plans")
public class GoAddonPlan extends BaseDateTime {
    @Id
    @CoreID(prefix = "NPLN", end = 7, replaceSymbols = true)
    @Column(nullable = false, columnDefinition = "TEXT", updatable = false)
    @GeneratedValue(generator = "addon_plan_gen")
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoAddonPlanInterval interval;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String currency;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            referencedColumnName = "id",
            name = "go_addon_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_addon_id_fkey")
    )
    private GoAddon addon;

    public String getAmt() {
        return getAmount().stripTrailingZeros().toPlainString();
    }

    public boolean isRecurring() {
        return getInterval() != GoAddonPlanInterval.ONCE;
    }
}