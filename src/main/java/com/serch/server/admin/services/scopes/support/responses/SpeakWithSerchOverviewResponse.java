package com.serch.server.admin.services.scopes.support.responses;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpeakWithSerchOverviewResponse {
    private List<SpeakWithSerchScopeResponse> assigned = new ArrayList<>();
    private List<SpeakWithSerchScopeResponse> others = new ArrayList<>();
}
