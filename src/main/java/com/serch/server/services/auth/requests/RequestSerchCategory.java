package com.serch.server.services.auth.requests;

import com.serch.server.enums.account.SerchCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestSerchCategory {
    private SerchCategory category;
    private String emailAddress;
}
