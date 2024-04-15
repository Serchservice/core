package com.serch.server.services.account.responses;

import lombok.Data;

import java.util.List;

@Data
public class RequestResponse {
    private List<RequestData> ongoing;
    private List<RequestData> pending;
}