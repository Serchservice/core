package com.serch.server.models.subscription;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.generators.subscription.PlanParentID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The Subscription class represents a subscription in the subscription schema. It stores information such as
 * the subscribed plan, subscription status, subscription date, retries, associated user, and invoices.
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
 *     <li>Many-to-one with {@link PlanParent} as the plan.</li>
 *     <li>Many-to-one with {@link PlanChild} as the sub plan.</li>
 *     <li>One-to-one with {@link SubscriptionAuth} as the auth.</li>
 *     <li>One-to-many with {@link SubscriptionInvoice} as the invoices.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "subscriptions")
public class Subscription extends BaseDateTime {
   @Id
   @Column(name = "id", nullable = false, columnDefinition = "TEXT", unique = true)
   @GenericGenerator(name = "plan_id_gen", type = PlanParentID.class)
   @GeneratedValue(generator = "plan_id_gen")
   private String id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "plan",
           referencedColumnName = "id",
           nullable = false,
           foreignKey = @ForeignKey(name = "plan_id_fkey")
   )
   private PlanParent plan;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "sub_plan",
           referencedColumnName = "id",
           foreignKey = @ForeignKey(name = "sub_plan_id_fkey")
   )
   private PlanChild child;

   @Column(name = "status", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanStatus planStatus = PlanStatus.ACTIVE;

   @Column(name = "free_status", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanStatus freePlanStatus = PlanStatus.NOT_USED;

   @Column(name = "subscribed_at", nullable = false)
   private LocalDateTime subscribedAt = LocalDateTime.now();

   @Column(name = "retries", nullable = false)
   private Integer retries = 0;

   @OneToOne(mappedBy = "subscription")
   private SubscriptionAuth auth;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "serch_id",
           referencedColumnName = "id",
           nullable = false,
           foreignKey = @ForeignKey(name = "plan_user_id_fkey")
   )
   private User user;

   @OneToMany(fetch = FetchType.LAZY, mappedBy = "subscription")
   private List<SubscriptionInvoice> invoices;

   /**
    * Checks if the subscription is currently active.
    *
    * @return true if the subscription is active, otherwise false.
    */
   public boolean isActive() {
      return planStatus == PlanStatus.ACTIVE;
   }

   /**
    * Checks if the subscription is expired.
    *
    * @return true if the subscription is expired, otherwise false.
    */
   public boolean isExpired() {
      return planStatus == PlanStatus.EXPIRED;
   }

   /**
    * Checks if the subscription's authentication signature is not the same as the given signature.
    *
    * @param signature the authentication signature to compare.
    * @return true if the subscription's authentication signature is different from the given signature, otherwise false.
    */
   public boolean isNotSameAuth(String signature) {
      return getAuth() != null && !getAuth().getSignature().equals(signature);
   }

   /**
    * Checks if the free plan associated with the subscription can be used.
    *
    * @return true if the free plan can be used, otherwise false.
    */
   public boolean canUseFreePlan() {
      return freePlanStatus == PlanStatus.NOT_USED;
   }
}
