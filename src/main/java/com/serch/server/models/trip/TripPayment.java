package com.serch.server.models.trip;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.generators.transaction.TransactionID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "payments")
public class TripPayment extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT", updatable = false)
    @GenericGenerator(name = "transaction_gen", type = TransactionID.class)
    @GeneratedValue(generator = "transaction_gen")
    private String id;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(unique = true, columnDefinition = "TEXT")
    private String url;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT", updatable = false)
    private String reference;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_authentication_id_fkey")
    )
    private Trip trip;
}
