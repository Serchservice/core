package com.serch.server.domains.transaction.services.implementations;

import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.models.account.Profile;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.domains.transaction.services.SchedulePayService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * The SchedulePayImplementation class provides implementation for scheduled payments.
 * @see SchedulePayService
 */
@Service
@RequiredArgsConstructor
public class SchedulePayImplementation implements SchedulePayService {
    private final NotificationService transactionNotification;
    private final WalletRepository walletRepository;
    private final ProfileRepository profileRepository;
    private final TransactionRepository transactionRepository;

    @Value("${application.account.schedule.close.charge}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_CHARGE;

    @Override
    public boolean charge(Schedule schedule) {
        Wallet wallet = wallet(schedule.getClosedBy());
        if(wallet != null) {
            Transaction transaction = getTransaction(schedule);

            wallet.setUncleared(wallet.getUncleared().add(transaction.getAmount()));
            wallet.setUpdatedAt(TimeUtil.now());
            walletRepository.save(wallet);

            return hasSufficientAmount(wallet, transaction.getAmount());
        }

        return false;
    }

    private Transaction getTransaction(Schedule schedule) {
        Transaction transaction = new Transaction();
        transaction.setSender(String.valueOf(schedule.getClosedBy()));
        transaction.setType(TransactionType.SCHEDULE);
        transaction.setAccount(String.valueOf(receiver(schedule)));
        transaction.setAmount(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE));
        transaction.setReference(HelperUtil.generateReference("SCHED"));
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setVerified(false);
        transaction.setEvent(schedule.getId());

        return transactionRepository.save(transaction);
    }

    private UUID receiver(Schedule schedule) {
        if(schedule.getUser().isSameAs(schedule.getClosedBy())) {
            if(schedule.getProvider().isAssociate()) {
                return schedule.getProvider().getBusiness().getId();
            }
            return schedule.getProvider().getId();
        }

        return schedule.getUser().getId();
    }

    private boolean hasSufficientAmount(Wallet wallet, BigDecimal amount) {
        /// Check if the balance/deposit is enough to cover the payment || deposit + balance can cover it
        return wallet.getBalance().compareTo(amount) > 0 || wallet.getDeposit().compareTo(amount) > 0
                || wallet.getDeposit().add(wallet.getBalance()).compareTo(amount) > 0;
    }

    private Wallet wallet(UUID closedBy) {
        Optional<Profile> profile = profileRepository.findById(closedBy);
        if(profile.isPresent()) {
            Optional<Wallet> wallet;
            if(profile.get().isAssociate()) {
                wallet = walletRepository.findByUser_Id(profile.get().getBusiness().getId());
            } else {
                wallet = walletRepository.findByUser_Id(profile.get().getId());
            }
            if(wallet.isPresent()) {
                return wallet.get();
            }
        }

        return null;
    }

    @Override
    @Transactional
    public void processPayments() {
        transactionRepository.findPendingSchedules().forEach(transaction -> {
            try {
                Wallet wallet = wallet(UUID.fromString(transaction.getSender()));
                if(wallet != null) {
                    // Check Balance first, then check deposit before checking deposit + balance
                    if(wallet.getBalance().compareTo(transaction.getAmount()) > 0) {
                        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
                        creditReceiverWallet(transaction);
                        updateSenderWallet(transaction, wallet);
                    } else if(wallet.getDeposit().compareTo(transaction.getAmount()) > 0) {
                        wallet.setDeposit(wallet.getDeposit().subtract(transaction.getAmount()));
                        creditReceiverWallet(transaction);
                        updateSenderWallet(transaction, wallet);
                    } else if(wallet.getDeposit().add(wallet.getBalance()).compareTo(transaction.getAmount()) > 0) {
                        /// Remove the amount that the deposit can transaction
                        transaction.setAmount(transaction.getAmount().subtract(wallet.getDeposit()));
                        /// Debit the amount remaining from the balance
                        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
                        /// Debit all from deposit
                        wallet.setDeposit(BigDecimal.ZERO);
                        creditReceiverWallet(transaction);
                        updateSenderWallet(transaction, wallet);
                    }
                }
            } catch (Exception ignored) { }
        });
    }

    private void updateSenderWallet(Transaction transaction, Wallet wallet) {
        wallet.setUpdatedAt(TimeUtil.now());
        wallet.setUncleared(wallet.getUncleared().subtract(transaction.getAmount()));
        walletRepository.save(wallet);

        transactionNotification.send(wallet.getUser().getId(), false, transaction.getAmount(), transaction.getId());

        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setUpdatedAt(TimeUtil.now());
        transaction.setVerified(true);
        transactionRepository.save(transaction);
    }

    private void creditReceiverWallet(Transaction transaction) {
        UUID id = UUID.fromString(transaction.getAccount());
        Wallet wallet = wallet(id);
        if(wallet != null) {
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            wallet.setUpdatedAt(TimeUtil.now());
            walletRepository.save(wallet);
            transactionNotification.send(id, true, transaction.getAmount(), transaction.getId());
        }
    }
}
