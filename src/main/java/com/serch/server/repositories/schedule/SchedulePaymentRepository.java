package com.serch.server.repositories.schedule;

import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.models.schedule.SchedulePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface SchedulePaymentRepository extends JpaRepository<SchedulePayment, String> {
    List<SchedulePayment> findByStatus(@NonNull TransactionStatus status);
    @Query("select p from SchedulePayment p where p.schedule.user.id = :id " +
            "or p.schedule.provider.id = :id or p.schedule.provider.business.id = :id " +
            "order by p.updatedAt asc"
    )
    List<SchedulePayment> findByPayment(@NonNull UUID id);
}