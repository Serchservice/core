//package com.serch.server.services.transaction.services.implementations;
//
//import com.serch.server.bases.ApiResponse;
//import com.serch.server.enums.auth.Role;
//import com.serch.server.enums.transaction.TransactionStatus;
//import com.serch.server.enums.transaction.TransactionType;
//import com.serch.server.exceptions.transaction.WalletException;
//import com.serch.server.models.conversation.Call;
//import com.serch.server.models.transaction.Transaction;
//import com.serch.server.models.transaction.Wallet;
//import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
//import com.serch.server.services.transaction.requests.PayRequest;
//import com.serch.server.services.transaction.services.TripPayService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class TripPayImplementation implements TripPayService {
//    @Value("${application.call.tip2fix.amount}")
//    private Integer TIP2FIX_AMOUNT;
//
//    @Override
//    public ApiResponse<String> pay(PayRequest request) {
//        if(request.getType() == TransactionType.T2F) {
//            return payTip2Fix(request);
//        } else if(request.getType() == TransactionType.TRIP) {
//            return payTrip(request);
//        } else {
//            return new ApiResponse<>("Error");
//        }
//    }
//
//    private ApiResponse<String> payTip2Fix(PayRequest request) {
//        Optional<Call> call = callRepository.findById(request.getEvent());
//        if(call.isPresent()) {
//            Optional<Wallet> sender = walletRepository.findByUser_Id(call.get().getCaller().getId());
//            if(sender.isPresent()) {
//                BalanceUpdateRequest senderUpdate = BalanceUpdateRequest.builder()
//                        .type(TransactionType.T2F)
//                        .user(call.get().getCaller().getId())
//                        .amount(BigDecimal.valueOf(TIP2FIX_AMOUNT))
//                        .build();
//                BalanceUpdateRequest receiverUpdate = BalanceUpdateRequest.builder()
//                        .type(TransactionType.T2F)
//                        .user(call.get().getCalled().getId())
//                        .amount(BigDecimal.valueOf(TIP2FIX_AMOUNT))
//                        .build();
//
//                if(call.get().getCalled().getUser().getRole() == Role.ASSOCIATE_PROVIDER) {
//                    Optional<Wallet> wallet = walletRepository.findByUser_Id(call.get().getCalled().getBusiness().getId());
//                    if(wallet.isPresent()) {
//                        receiverUpdate.setUser(call.get().getCalled().getBusiness().getId());
//                        Transaction transaction = processTip2Fix(
//                                sender.get(), call.get(), senderUpdate,
//                                receiverUpdate, wallet.get()
//                        );
////                        transaction.setAssociate(call.get().getCalled());
//                        transactionRepository.save(transaction);
//                    } else {
//                        return new ApiResponse<>("Recipient not found");
//                    }
//                } else {
//                    Optional<Wallet> wallet = walletRepository.findByUser_Id(call.get().getCalled().getId());
//                    if(wallet.isPresent()) {
//                        Transaction transaction = processTip2Fix(
//                                sender.get(), call.get(), senderUpdate,
//                                receiverUpdate, wallet.get()
//                        );
//                        transactionRepository.save(transaction);
//                    } else {
//                        return new ApiResponse<>("Recipient not found");
//                    }
//                }
//            } else {
//                return new ApiResponse<>("Caller not found");
//            }
//        } else {
//            return new ApiResponse<>("Call not found");
//        }
//        return new ApiResponse<>("Payment successful", HttpStatus.OK);
//    }
//
//    private Transaction processTip2Fix(
//            Wallet sender, Call call, BalanceUpdateRequest senderUpdate,
//            BalanceUpdateRequest receiverUpdate, Wallet receiver
//    ) {
//        util.updateBalance(senderUpdate);
//        util.updateBalance(receiverUpdate);
//
//        Transaction transaction = new Transaction();
//        transaction.setAmount(BigDecimal.valueOf(TIP2FIX_AMOUNT));
//        transaction.setType(TransactionType.T2F);
//        transaction.setVerified(true);
//        transaction.setStatus(TransactionStatus.SUCCESSFUL);
//        transaction.setAccount(receiver.getId());
//        transaction.setSender(sender);
//        transaction.setCall(call);
//        transaction.setReference(generateReference());
//        return transaction;
//    }
//
//    @Override
//    public ApiResponse<String> payTrip(PayRequest request) {
//        log.error("Pay trip in wallet implementation is yet to be implemented");
//        throw new WalletException("Unimplemented error");
//    }
//
//    @Override
//    public ApiResponse<String> checkIfUserCanPayForTripWithWallet(String trip) {
//        return null;
//    }
//}
