package com.serch.server.models.subscription;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.generators.PlanParentID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "subscriptions")
public class Subscription extends BaseDateTime {
   @Id
   @Column(name = "id", nullable = false, columnDefinition = "TEXT")
   @GenericGenerator(name = "plan_id_gen", type = PlanParentID.class)
   @GeneratedValue(generator = "plan_id_gen")
   private String id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "plan",
           referencedColumnName = "type",
           nullable = false,
           foreignKey = @ForeignKey(name = "plan_type_fkey")
   )
   private PlanParent plan;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "sub_plan",
           referencedColumnName = "type",
           foreignKey = @ForeignKey(name = "sub_plan_type_fkey")
   )
   private PlanChild child;

   @Column(name = "plan_status", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanStatus planStatus = PlanStatus.ACTIVE;

   @Column(name = "free_plan_status", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanStatus freePlanStatus = PlanStatus.NOT_USED;

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

   public boolean isActive() {
      return planStatus == PlanStatus.ACTIVE;
   }
   public boolean isExpired() {
      return planStatus == PlanStatus.INACTIVE;
   }
   public boolean isNotSameAuth(String signature) {
      return getAuth() != null && !getAuth().getSignature().equals(signature);
   }
   public boolean canUseFreePlan() {
      return freePlanStatus == PlanStatus.NOT_USED;
   }
}
