package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.CompanyException;
import com.serch.server.models.auth.User;
import com.serch.server.models.company.Issue;
import com.serch.server.models.company.Product;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.company.IssueRepository;
import com.serch.server.repositories.company.ProductRepository;
import com.serch.server.services.company.requests.IssueRequest;
import com.serch.server.services.company.services.IssueService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueImplementation implements IssueService {
    private final UserUtil userUtil;
    private final ProductRepository productRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<String> lodgeIssue(IssueRequest request) {
        Optional<User> user = userRepository.findByEmailAddress(UserUtil.getLoginUser());
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new CompanyException("Product does not exist anymore"));

        Issue issue = new Issue();
        issue.setComment(request.getComment());
        issue.setProduct(product);

        if(user.isPresent()) {
            issue.setUser(user.get());
            var saved = issueRepository.save(issue);
            return new ApiResponse<>(
                    "Issue submitted. You can track this issue with ID: %s".formatted(saved.getTicket()),
                    HttpStatus.CREATED
            );
        } else {
            var saved = issueRepository.save(issue);
            return new ApiResponse<>(
                    "Issue submitted. You can track this issue with ID: %s".formatted(saved.getTicket()),
                    HttpStatus.CREATED
            );
        }
    }
}
