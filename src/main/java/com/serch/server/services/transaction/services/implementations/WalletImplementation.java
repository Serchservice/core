package com.serch.server.services.transaction.services.implementations;

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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public void payTip2Fix(PayRequest request) {
        Wallet sender = walletRepository.findByUser_Id(request.getSender())
                .orElseThrow(() -> new WalletException("User not found"));
        User receiver = userRepository.findById(request.getReceiver())
                .orElseThrow(() -> new WalletException("Recipient not found"));
        Call call = callRepository.findById(request.getEvent())
                .orElseThrow(() -> new WalletException("Call not found"));

        BalanceUpdateRequest senderRequest = BalanceUpdateRequest.builder()
                .type(TransactionType.T2F)
                .user(sender.getUser().getId())
                .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                .build();
        BalanceUpdateRequest receiverRequest = BalanceUpdateRequest.builder()
                .type(TransactionType.T2F)
                .user(receiver.getId())
                .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                .build();

        if(receiver.getRole() == Role.ASSOCIATE_PROVIDER) {
            Profile profile =  profileRepository.findById(receiver.getId())
                    .orElseThrow(() -> new WalletException("Recipient not found"));
            Wallet wallet = walletRepository.findByUser_Id(profile.getBusiness().getSerchId())
                    .orElseThrow(() -> new WalletException("Recipient not found"));

            receiverRequest.setUser(profile.getBusiness().getSerchId());
            Transaction transaction = processTip2Fix(sender, call, senderRequest, receiverRequest, wallet);
            transaction.setAssociate(profile);
            transactionRepository.save(transaction);
        } else {
            Wallet wallet = walletRepository.findByUser_Id(request.getReceiver())
                    .orElseThrow(() -> new WalletException("Recipient not found"));

            Transaction transaction = processTip2Fix(sender, call, senderRequest, receiverRequest, wallet);
            transactionRepository.save(transaction);
        }
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
