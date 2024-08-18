package com.serch.server.admin.services.scopes.marketing;

import com.serch.server.admin.mappers.AdminCompanyMapper;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.marketing.responses.MarketingResponse;
import com.serch.server.admin.services.scopes.marketing.responses.NewsletterResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.NewsletterStatus;
import com.serch.server.models.company.Newsletter;
import com.serch.server.repositories.company.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MarketingScopeImplementation implements MarketingScopeService {
    private final NewsletterRepository newsletterRepository;

    @Override
    @Transactional
    public ApiResponse<MarketingResponse> overview() {
        List<Newsletter> newsletters = newsletterRepository.findAll(PageRequest.of(0, 10)).toList();

        MarketingResponse response = new MarketingResponse();
        List<Metric> metrics = getMetrics();

        if(!newsletters.isEmpty()) {
            response.setRecentSubscriptions(newsletters.stream()
                    .sorted(Comparator.comparing(Newsletter::getUpdatedAt).reversed())
                    .map(AdminCompanyMapper.instance::response).toList()
            );
        }
        response.setMetrics(metrics);
        return new ApiResponse<>(response);
    }

    private List<Metric> getMetrics() {
        List<Metric> metrics = new ArrayList<>();

        List<Newsletter> newsletters = newsletterRepository.findAll();
        Metric allMetric = new Metric();
        allMetric.setHeader("All");
        allMetric.setCount("%s".formatted(!newsletters.isEmpty() ? newsletters.size() : 0));
        allMetric.setFeature("ALL");
        metrics.add(allMetric);

        Arrays.stream(NewsletterStatus.values()).toList().forEach(status -> {
            List<Newsletter> statusList = newsletterRepository.findByStatus(status);
            Metric metric = new Metric();
            metric.setHeader(status.getValue());
            metric.setCount("%s".formatted(!statusList.isEmpty() ? statusList.size() : 0));
            metric.setFeature(status.name());
            metrics.add(metric);
        });
        return metrics;
    }

    @Override
    @Transactional
    public ApiResponse<List<NewsletterResponse>> newsletters() {
        List<Newsletter> newsletters = newsletterRepository.findAll();
        return new ApiResponse<>(newsletters.stream()
                .sorted(Comparator.comparing(Newsletter::getUpdatedAt).reversed())
                .map(AdminCompanyMapper.instance::response).toList()
        );
    }

    @Override
    @Transactional
    public ApiResponse<List<NewsletterResponse>> update(Long id) {
        Newsletter letter = newsletterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid id"));
        letter.setUpdatedAt(LocalDateTime.now());
        letter.setStatus(letter.getStatus() == NewsletterStatus.COLLECTED ? NewsletterStatus.UNCOLLECTED : NewsletterStatus.COLLECTED);
        newsletterRepository.save(letter);
        return newsletters();
    }
}
