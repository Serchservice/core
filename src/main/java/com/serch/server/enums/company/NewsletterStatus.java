package com.serch.server.enums.company;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsletterStatus {
    COLLECTED("Collected"),
    UNCOLLECTED("Uncollected");

    private final String value;
}
