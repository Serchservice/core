package com.serch.server.domains.trip.services.implementations;

import com.serch.server.core.notification.core.NotificationService;
import com.serch.server.core.payment.core.PaymentService;
import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.models.trip.ShoppingItem;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripPayment;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.repositories.trip.TripPaymentRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.domains.trip.services.TripPayService;
import com.serch.server.domains.trip.services.TripTimelineService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.serch.server.enums.transaction.TransactionStatus.FAILED;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;
import static com.serch.server.enums.transaction.TransactionType.*;
import static com.serch.server.enums.trip.TripConnectionStatus.CONNECTED;
import static com.serch.server.enums.trip.TripConnectionStatus.REQUESTED;

@Service
@RequiredArgsConstructor
public class TripPayImplementation implements TripPayService {
    @Value("${application.payment.trip.service.fee}")
    private Integer TRIP_SERVICE_FEE;

    @Value("${application.payment.trip.service.user}")
    private Integer TRIP_SERVICE_USER;

    @Value("${application.trip.count.min}")
    private Integer TRIP_MIN_COUNT_BEFORE_CHARGE;

    private final PaymentService paymentService;
    private final TripTimelineService timelineService;
    private final NotificationService notificationService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TripRepository tripRepository;
    private final TripPaymentRepository tripPaymentRepository;
    private final ProfileRepository profileRepository;
    private final GuestRepository guestRepository;

    @Override
    @Transactional
    public Boolean processPayment(Trip trip) {
        if(trip.withUserShare()) {
            BigDecimal amount = BigDecimal.valueOf(TRIP_SERVICE_FEE).add(BigDecimal.valueOf(TRIP_SERVICE_USER));
            if(isBalanceSufficient(trip, amount)) {
                Wallet wallet = getWallet(trip);
                if(wallet != null) {
                    Transaction transaction = processPayment(trip, wallet, amount);

                    transaction.setAmount(BigDecimal.valueOf(TRIP_SERVICE_USER));
                    transaction.setType(TRIP_SHARE);
                    transaction.setAccount(String.valueOf(trip.getShared().getSharedLink().getUser().getId()));
                    transaction.setReference(HelperUtil.generateReference("STR-TRIP"));
                    transaction.setEvent(trip.getId());
                    transactionRepository.save(transaction);

                    trip.setUserShare(BigDecimal.valueOf(TRIP_SERVICE_USER));
                    tripRepository.save(trip);

                    walletRepository.findByUser_Id(trip.getShared().getSharedLink().getUser().getId())
                            .ifPresent(userWallet -> {
                                userWallet.setBalance(userWallet.getBalance().add(BigDecimal.valueOf(TRIP_SERVICE_USER)));
                                userWallet.setUpdatedAt(TimeUtil.now());
                                walletRepository.save(userWallet);

                                notificationService.send(trip.getShared().getSharedLink().getUser().getId(), true, BigDecimal.valueOf(TRIP_SERVICE_USER));
                            });

                    return true;
                }
            }
        } else {
            if(isBalanceSufficient(trip, BigDecimal.valueOf(TRIP_SERVICE_FEE))) {
                Wallet wallet = getWallet(trip);
                if(wallet != null) {
                    processPayment(trip, wallet, BigDecimal.valueOf(TRIP_SERVICE_FEE));
                    return true;
                }
            }
        }

        return false;
    }

    @Transactional
    protected Wallet getWallet(Trip trip) {
        Wallet wallet;

        if(trip.getProvider().isAssociate()) {
            wallet = walletRepository.findByUser_Id(trip.getProvider().getBusiness().getId()).orElse(null);
        } else {
            wallet = walletRepository.findByUser_Id(trip.getProvider().getId()).orElse(null);
        }
        return wallet;
    }

    @Transactional
    protected Transaction processPayment(Trip trip, Wallet wallet, BigDecimal debit) {
        String reference = HelperUtil.generateReference("STR-TRIP");

        if(wallet.getDeposit().compareTo(debit) >= 0) {
            wallet.setDeposit(wallet.getDeposit().subtract(debit));
        } else {
            debit = debit.subtract(wallet.getDeposit());
            wallet.setBalance(wallet.getBalance().subtract(debit));
            wallet.setDeposit(BigDecimal.ZERO);
        }
        wallet.setUpdatedAt(TimeUtil.now());
        walletRepository.save(wallet);

        trip.setServiceFee(BigDecimal.valueOf(TRIP_SERVICE_FEE));
        trip.setServiceFeeReference(reference);
        tripRepository.save(trip);

        notificationService.send(wallet.getUser().getId(), false, debit);

        Transaction transaction = getTransaction(trip, reference);
        return transactionRepository.save(transaction);
    }

    private Transaction getTransaction(Trip trip, String reference) {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(TRIP_SERVICE_FEE));
        transaction.setType(TRIP_CHARGE);
        transaction.setVerified(true);
        transaction.setStatus(SUCCESSFUL);
        transaction.setMode("WALLET");

        if(trip.getProvider().isAssociate()) {
            transaction.setAccount(String.valueOf(trip.getProvider().getBusiness().getId()));
            transaction.setSender(String.valueOf(trip.getProvider().getBusiness().getId()));
        } else {
            transaction.setAccount(String.valueOf(trip.getProvider().getId()));
            transaction.setSender(String.valueOf(trip.getProvider().getId()));
        }
        transaction.setEvent(trip.getId());
        transaction.setReference(reference);
        return transaction;
    }

    private boolean isBalanceSufficient(Trip trip, BigDecimal amount) {
        UUID provider = trip.getProvider().isAssociate()
                ? trip.getProvider().getBusiness().getId()
                : trip.getProvider().getId();

        return walletRepository.findByUser_Id(provider).map(wallet -> {
            BigDecimal total = wallet.getDeposit().add(wallet.getBalance());
            return total.compareTo(amount) > 0;
        }).orElse(false);
    }

    @Override
    @Transactional
    public InitializePaymentData initializeShoppingPayment(Trip trip) {
        if(trip.getShoppingItems() != null && !trip.getShoppingItems().isEmpty()) {
            BigDecimal total = trip.getShoppingItems().stream()
                    .map(ShoppingItem::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(trip.getAmount());

            TripPayment payment = new TripPayment();
            payment.setTrip(trip);
            payment.setAmount(total);

            String emailAddress;
            try {
                emailAddress = profileRepository.findById(UUID.fromString(trip.getAccount()))
                        .map(profile -> profile.getUser().getEmailAddress())
                        .orElse(null);
            } catch (Exception ignored) {
                emailAddress = guestRepository.findById(trip.getAccount())
                        .map(Guest::getEmailAddress)
                        .orElse(null);
            }

            if(emailAddress != null) {
                InitializePaymentRequest paymentRequest = new InitializePaymentRequest();
                paymentRequest.setAmount(String.valueOf(total.intValue()));
                paymentRequest.setEmail(emailAddress);
                paymentRequest.setCallbackUrl("");

                InitializePaymentData data = paymentService.initialize(paymentRequest);
                payment.setReference(data.getReference());
                tripPaymentRepository.save(payment);

                saveShoppingPayment(trip, total, data);

                return data;
            }
        }
        return null;
    }

    private void saveShoppingPayment(Trip trip, BigDecimal total, InitializePaymentData data) {
        Transaction transaction = new Transaction();
        transaction.setAmount(total);
        transaction.setType(SHOPPING);
        transaction.setAccount(String.valueOf(trip.getProvider().getId()));
        transaction.setMode("CARD");
        transaction.setEvent(trip.getId());
        transaction.setSender(trip.getAccount());
        transaction.setReference(data.getReference());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public InitializePaymentData pay(Trip trip, UUID id) {
        List<Trip> trips = tripRepository.findByProvider_Id(id);

        if(trips.size() >= TRIP_MIN_COUNT_BEFORE_CHARGE) {
            Boolean isPaid = processPayment(trip);
            if(isPaid) {
                timelineService.create(trip, null, CONNECTED);
            } else {
                timelineService.create(trip, null, REQUESTED);
            }

            trip.setIsServiceFeePaid(isPaid);
        } else {
            timelineService.create(trip, null, CONNECTED);

            trip.setIsServiceFeePaid(true);
        }
        tripRepository.save(trip);

        InitializePaymentData data = null;
        if(trip.getCategory() == SerchCategory.PERSONAL_SHOPPER) {
            data = initializeShoppingPayment(trip);
        }

        return data;
    }

    @Override
    @Transactional
    public Boolean verify(String reference) {
        TripPayment payment = tripPaymentRepository.findByReference(reference).orElse(null);
        Transaction transaction = transactionRepository.findByReference(reference).orElse(null);

        if(payment != null && transaction != null) {
            try {
                paymentService.verify(payment.getReference());
                payment.setStatus(SUCCESSFUL);
                payment.setUpdatedAt(TimeUtil.now());
                tripPaymentRepository.save(payment);

                transaction.setStatus(SUCCESSFUL);
                transaction.setVerified(true);
                transaction.setUpdatedAt(TimeUtil.now());
                transactionRepository.save(transaction);
                return true;
            } catch (Exception ignored) {
                payment.setStatus(FAILED);
                payment.setUpdatedAt(TimeUtil.now());
                tripPaymentRepository.save(payment);

                transaction.setStatus(FAILED);
                transaction.setVerified(false);
                transaction.setReason("Error in verification");
                transaction.setUpdatedAt(TimeUtil.now());
                transactionRepository.save(transaction);
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void processCredit(Trip trip) {
        TripPayment payment = trip.getPayment();
        if(payment != null && payment.getStatus() == SUCCESSFUL) {
            Transaction transaction = transactionRepository.findByEvent(trip.getId()).orElse(null);
            if(transaction != null && transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                walletRepository.findByUser_Id(trip.getProvider().getId())
                        .ifPresent(wallet -> {
                            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                            wallet.setUpdatedAt(TimeUtil.now());
                            walletRepository.save(wallet);
                        });
            }
        }
    }
}
