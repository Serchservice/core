package com.serch.server.services.trip.responses;

import com.serch.server.services.shop.responses.SearchShopResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponse {
    private ActiveResponse best;
    private List<ActiveResponse> providers = new ArrayList<>();
    private List<SearchShopResponse> shops = new ArrayList<>();
}