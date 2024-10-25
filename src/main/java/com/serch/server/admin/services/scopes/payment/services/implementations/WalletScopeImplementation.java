package com.serch.server.admin.services.scopes.payment.services.implementations;

import com.serch.server.admin.mappers.ScopeMapper;
import com.serch.server.admin.services.scopes.common.CommonProfileService;
import com.serch.server.admin.services.scopes.payment.responses.transactions.PaymentApiResponse;
import com.serch.server.admin.services.scopes.payment.responses.wallet.WalletScopeResponse;
import com.serch.server.admin.services.scopes.payment.services.WalletScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.mappers.TransactionMapper;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.responses.WalletResponse;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletScopeImplementation implements WalletScopeService {
    private final CommonProfileService profileService;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    @Override
    public ApiResponse<WalletScopeResponse> wallet(String id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(() -> new SerchException("Wallet not found"));

        return new ApiResponse<>(getWalletResponse(wallet));
    }

    @Override
    public ApiResponse<PaymentApiResponse<WalletScopeResponse>> wallets(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        PaymentApiResponse<WalletScopeResponse> response = new PaymentApiResponse<>();
        response.setTotal(walletRepository.count());
        response.setTransactions(walletRepository.findAll(pageable)
                .stream()
                .map(this::getWalletResponse).toList());

        return new ApiResponse<>(response);
    }

    private WalletScopeResponse getWalletResponse(Wallet wallet) {
        WalletScopeResponse response = ScopeMapper.instance.wallet(wallet);

        WalletResponse info = TransactionMapper.INSTANCE.wallet(wallet);
        info.setBalance(MoneyUtil.formatToNaira(wallet.getBalance()));
        info.setDeposit(MoneyUtil.formatToNaira(wallet.getDeposit()));
        info.setUncleared(MoneyUtil.formatToNaira(wallet.getUncleared()));
        info.setPayout(MoneyUtil.formatToNaira(wallet.getPayout()));
        info.setNextPayday(walletService.formatNextPayday(wallet));
        info.setWallet(wallet.getId());

        response.setWallet(info);
        response.setProfile(profileService.fromUser(wallet.getUser()));

        return response;
    }
}