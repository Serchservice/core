package com.serch.server.services.conversation.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.conversation.requests.CheckTip2FixSessionRequest;
import com.serch.server.services.conversation.services.Tip2FixService;
import com.serch.server.utils.CallUtil;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Tip2FixImplementation implements Tip2FixService {
    private final WalletUtil walletUtil;
    private final CallRepository callRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Value("${serch.tip2fix.call-amount}")
    private Integer CALL_LIMIT;

    @Override
    public ApiResponse<String> checkSession(CheckTip2FixSessionRequest request) {
        Call call = callRepository.findById(request.getChannel())
                .orElseThrow(() -> new CallException("Call not found"));

        if(CallUtil.getHours(request.getDuration()) == 1) {
            if(walletUtil.isBalanceSufficient(BigDecimal.valueOf(CALL_LIMIT))) {
                pay(call.getCaller().getSerchId(), call.getCalled().getSerchId());
                call.setSessionCount(call.getSessionCount() + 1);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);

                return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
            } else {
                call.setStatus(CallStatus.CLOSED);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);

                throw new CallException("Your balance is too low to continue with call");
            }
        } else {
            return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
        }
    }

    @Override
    public void pay(UUID senderId, UUID receiverId) {
        var sender = walletRepository.findByUser_Id(senderId)
                .orElseThrow(() -> new CallException("User not found"));
        var receiver = walletRepository.findByProfile_SerchId(senderId)
                .orElseThrow(() -> new CallException("User not found"));

        sender.setBalance(sender.getBalance().subtract(BigDecimal.valueOf(CALL_LIMIT)));
        walletRepository.save(sender);

        receiver.setBalance(receiver.getBalance().add(BigDecimal.valueOf(CALL_LIMIT)));
        walletRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setReceiver(receiver);
        transaction.setSender(sender);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setType(TransactionType.TIP2FIX);
        transaction.setAmount(BigDecimal.valueOf(CALL_LIMIT));
        transactionRepository.save(transaction);
    }
}
