package com.serch.server.services.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.schedule.SchedulePayment;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.schedule.SchedulePaymentRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
import com.serch.server.services.transaction.services.SchedulePayService;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchedulePayImplementation implements SchedulePayService {
    private final WalletUtil walletUtil;
    private final SchedulePaymentRepository schedulePaymentRepository;

    @Value("${application.account.schedule.close.charge}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_CHARGE;
    private final WalletRepository walletRepository;

    @Override
    public ApiResponse<Boolean> charge(Schedule schedule) {
        SchedulePayment payment = new SchedulePayment();
        payment.setSchedule(schedule);
        payment.setAmount(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE));
        payment.setStatus(TransactionStatus.PENDING);
        schedulePaymentRepository.save(payment);

        BalanceUpdateRequest request = BalanceUpdateRequest.builder()
                .user(schedule.getClosedBy())
                .amount(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE))
                .type(TransactionType.WITHDRAW)
                .build();

        Optional<Wallet> wallet = walletRepository.findByUser_Id(schedule.getClosedBy());

        if(wallet.isPresent()) {
            wallet.get().setUncleared(wallet.get().getUncleared().add(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE)));
            walletRepository.save(wallet.get());
        }
        if(walletUtil.isBalanceSufficient(request)) {
            return new ApiResponse<>(true);
        } else {
            return new ApiResponse<>(false);
        }
    }

    @Override
    public void pay() {
        schedulePaymentRepository.findByStatus(TransactionStatus.PENDING)
                .forEach(pay -> {
                    BalanceUpdateRequest request = BalanceUpdateRequest.builder()
                            .user(pay.getSchedule().getClosedBy())
                            .amount(pay.getAmount())
                            .type(TransactionType.WITHDRAW)
                            .build();
                    if(walletUtil.isBalanceSufficient(request)) {
                        walletUtil.updateBalance(request);

                        request.setUser(
                                pay.getSchedule().getUser().isSameAs(pay.getSchedule().getClosedBy())
                                        ? pay.getSchedule().getProvider().getSerchId()
                                        : pay.getSchedule().getUser().getSerchId()
                        );
                        request.setType(TransactionType.FUNDING);
                        walletUtil.updateBalance(request);

                        pay.setStatus(TransactionStatus.SUCCESSFUL);
                        pay.setUpdatedAt(LocalDateTime.now());
                        schedulePaymentRepository.save(pay);

                        Optional<Wallet> wallet = walletRepository.findByUser_Id(pay.getSchedule().getClosedBy());

                        if(wallet.isPresent()) {
                            wallet.get().setUncleared(wallet.get().getUncleared().subtract(pay.getAmount()));
                            walletRepository.save(wallet.get());
                        }
                    }
                });
    }
}
