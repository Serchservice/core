package com.serch.server.services.account.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSpecialtyResponse implements Serializable {
    private Long id;
    private String specialty;
}