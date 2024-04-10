package com.serch.server.services.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
import com.serch.server.services.transaction.requests.PayRequest;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletImplementation implements WalletService {
    private final WalletUtil util;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CallRepository callRepository;
    private final TransactionRepository transactionRepository;
    private final ProfileRepository profileRepository;

    @Value("${serch.wallet.fund-amount-limit}")
    private Integer FUND_AMOUNT_LIMIT;
    @Value("${serch.tip2fix.call-amount}")
    private Integer TIP2FIX_CALL_AMOUNT;
    @Value("${serch.wallet.withdraw-amount-limit}")
    private Integer WITHDRAW_AMOUNT_LIMIT;

    @Override
    public void createWallet(User user) {
        if(walletRepository.existsByUser_Id(user.getId())) {
            throw new WalletException("User already owns a wallet");
        }

        if(user.getRole() != Role.ASSOCIATE_PROVIDER) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.valueOf(0.00));
            wallet.setDeposit(BigDecimal.valueOf(0.00));
            walletRepository.save(wallet);
        }
    }

    @Override
    public ApiResponse<String> pay(PayRequest request) {
        if(request.getType() == TransactionType.T2F) {
            return payTip2Fix(request);
        } else {
            return new ApiResponse<>("Error");
        }
    }

    private ApiResponse<String> payTip2Fix(PayRequest request) {
        Optional<Wallet> sender = walletRepository.findByUser_Id(request.getSender());
        if(sender.isPresent()) {
            Optional<User> receiver = userRepository.findById(request.getReceiver());
            if(receiver.isPresent()) {
                Optional<Call> call = callRepository.findById(request.getEvent());
                if(call.isPresent()) {
                    BalanceUpdateRequest senderRequest = BalanceUpdateRequest.builder()
                            .type(TransactionType.T2F)
                            .user(sender.get().getUser().getId())
                            .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                            .build();
                    BalanceUpdateRequest receiverRequest = BalanceUpdateRequest.builder()
                            .type(TransactionType.T2F)
                            .user(receiver.get().getId())
                            .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                            .build();

                    if(receiver.get().getRole() == Role.ASSOCIATE_PROVIDER) {
                        Optional<Profile> profile =  profileRepository.findById(receiver.get().getId());
                        if(profile.isPresent()) {
                            Optional<Wallet> wallet = walletRepository.findByUser_Id(profile.get().getBusiness().getSerchId());
                            if(wallet.isPresent()) {
                                receiverRequest.setUser(profile.get().getBusiness().getSerchId());
                                Transaction transaction = processTip2Fix(
                                        sender.get(), call.get(), senderRequest,
                                        receiverRequest, wallet.get()
                                );
                                transaction.setAssociate(profile.get());
                                transactionRepository.save(transaction);
                            } else {
                                return new ApiResponse<>("Recipient not found");
                            }
                        } else {
                            return new ApiResponse<>("Recipient not found");
                        }
                    } else {
                        Optional<Wallet> wallet = walletRepository.findByUser_Id(request.getReceiver());
                        if(wallet.isPresent()) {
                            Transaction transaction = processTip2Fix(
                                    sender.get(), call.get(), senderRequest,
                                    receiverRequest, wallet.get()
                            );
                            transactionRepository.save(transaction);
                        } else {
                            return new ApiResponse<>("Recipient not found");
                        }
                    }
                } else {
                    return new ApiResponse<>("Call not found");
                }
            } else {
                return new ApiResponse<>("Recipient not found");
            }
        } else {
            return new ApiResponse<>("User not found");
        }
        return new ApiResponse<>("Payment successful", HttpStatus.OK);
    }

    private Transaction processTip2Fix(
            Wallet sender, Call call, BalanceUpdateRequest senderRequest,
            BalanceUpdateRequest receiverRequest, Wallet wallet
    ) {
        util.updateBalance(senderRequest);
        util.updateBalance(receiverRequest);

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT));
        transaction.setType(TransactionType.T2F);
        transaction.setVerified(true);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setAccount(wallet.getId());
        transaction.setSender(sender);
        transaction.setCall(call);
        transaction.setReference("STR_%s".formatted(UUID.randomUUID().toString().substring(0, 8)));
        return transaction;
    }
}
