package com.serch.server.admin.services.account.responses;

import com.serch.server.enums.auth.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyStructure {
    private String id;
    private String name;
    private String position;
    private String image;
    private Role role;
    private List<CompanyStructure> children = new ArrayList<>();

    public void addChild(CompanyStructure child) {
        this.children.add(child);
    }
}