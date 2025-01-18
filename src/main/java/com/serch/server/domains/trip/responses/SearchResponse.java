package com.serch.server.domains.trip.responses;

import com.serch.server.domains.shop.responses.SearchShopResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponse {
    private ActiveResponse best;
    private List<ActiveResponse> providers = new ArrayList<>();
    private List<SearchShopResponse> shops = new ArrayList<>();
}