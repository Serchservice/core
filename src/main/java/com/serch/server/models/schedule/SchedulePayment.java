package com.serch.server.models.schedule;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.generators.schedule.SchedulePayID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

/**
 * The SchedulePayment class represents payments associated with closed schedules.
 * It stores information about the payment amount, status, and the associated schedule.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link Schedule} - The schedule associated with the payment.</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link TransactionStatus} - Represents the status of the transaction.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "closed_schedule_payments")
public class SchedulePayment extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "schedule_pay_seq", type = SchedulePayID.class)
    @GeneratedValue(generator = "schedule_pay_seq")
    private String id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Transaction Status must be an enum")
    private TransactionStatus status = TransactionStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "schedule_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "pay_schedule_id_fkey")
    )
    private Schedule schedule;
}
