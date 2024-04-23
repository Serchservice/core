package com.serch.server.enums.company;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamType {
    EXECUTIVE("Executive"),
    BOARD("Board");

    private final String type;
}
