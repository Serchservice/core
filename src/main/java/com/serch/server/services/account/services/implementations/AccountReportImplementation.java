package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.AccountReport;
import com.serch.server.models.auth.User;
import com.serch.server.models.shop.Shop;
import com.serch.server.repositories.account.AccountReportRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.services.account.requests.AccountReportRequest;
import com.serch.server.services.account.services.AccountReportService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service implementation for reporting user accounts or shops.
 * It implements its wrapper class {@link AccountReportService}
 *
 * @see UserUtil
 * @see UserRepository
 * @see AccountReportRepository
 */
@Service
@RequiredArgsConstructor
public class AccountReportImplementation implements AccountReportService {
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final AccountReportRepository accountReportRepository;

    @Value("${application.account.report-limit}")
    private Integer REPORT_LIMIT;
    private final ShopRepository shopRepository;

    @Override
    public ApiResponse<String> report(AccountReportRequest request) {
        if(request.getContent() != null && !request.getContent().isEmpty()) {
            if(request.getShop() != null && !request.getShop().isEmpty()) {
                Shop reported = shopRepository.findById(request.getShop())
                        .orElseThrow(() -> new AccountException("Shop not found"));

                AccountReport report = new AccountReport();
                report.setAccount(String.valueOf(reported.getId()));
                report.setUser(userUtil.getUser());
                report.setComment(request.getContent());
                report.setStatus(IssueStatus.OPENED);
                accountReportRepository.save(report);

                if(accountReportRepository.findByShop_Id(reported.getId()).size() >= REPORT_LIMIT) {
                    reported.setStatus(ShopStatus.SUSPENDED);
                    reported.setUpdatedAt(LocalDateTime.now());
                    shopRepository.save(reported);
                }
            } else {
                User reported = userRepository.findById(request.getId())
                        .orElseThrow(() -> new AccountException("User not found"));

                AccountReport report = new AccountReport();
                report.setAccount(String.valueOf(reported.getId()));
                report.setUser(userUtil.getUser());
                report.setComment(request.getContent());
                report.setStatus(IssueStatus.OPENED);
                accountReportRepository.save(report);

                if(accountReportRepository.findByReported_Id(reported.getId()).size() >= REPORT_LIMIT) {
                    reported.setAccountStatus(AccountStatus.HAS_REPORTED_ISSUES);
                    reported.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(reported);
                }
            }
            return new ApiResponse<>("Report logged", HttpStatus.OK);
        } else {
            if(request.getShop() != null && !request.getShop().isEmpty()) {
                throw new AccountException("We need to know why you are reporting this shop");
            } else {
                throw new AccountException("We need to know why you are reporting this account");
            }
        }
    }
}
