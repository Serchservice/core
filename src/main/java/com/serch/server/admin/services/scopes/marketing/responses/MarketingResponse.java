package com.serch.server.admin.services.scopes.marketing.responses;

import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MarketingResponse {
    private List<Metric> metrics = new ArrayList<>();
    private List<NewsletterResponse> recentSubscriptions = new ArrayList<>();
}
