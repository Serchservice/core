package com.serch.server.services.company.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class IssueRequest {
    private String ticket;
    private String comment;
}