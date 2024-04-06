package com.serch.server.models.subscription;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.subscription.SubPlanType;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "plans")
public class Plan extends BaseModel {
   @Column(name = "plan", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanType plan;

   @Column(name = "sub_plan")
   @Enumerated(value = EnumType.STRING)
   private SubPlanType subPlan = null;

   @Column(name = "plan_status", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanStatus planStatus = PlanStatus.ACTIVE;

   @Column(name = "free_plan_status", nullable = false)
   @Enumerated(value = EnumType.STRING)
   @SerchEnum(message = "PlanType must be an enum")
   private PlanStatus freePlanStatus = PlanStatus.NOT_USED;

   @OneToOne(mappedBy = "plan")
   private SubscriptionAuth auth;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "serch_id",
           referencedColumnName = "serch_id",
           nullable = false,
           foreignKey = @ForeignKey(name = "plan_serch_id_fkey")
   )
   private Profile profile;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(
           name = "business_id",
           nullable = false,
           referencedColumnName = "serch_id",
           foreignKey = @ForeignKey(name = "plan_business_id_fkey")
   )
   private BusinessProfile businessProfile;

   public boolean isActive() {
      return planStatus == PlanStatus.ACTIVE;
   }
   public boolean canUseFreePlan() {
      return freePlanStatus == PlanStatus.NOT_USED && businessProfile == null;
   }
}
