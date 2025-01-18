package com.serch.server.domains.account.requests;

import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.auth.User;
import com.serch.server.domains.auth.requests.RequestProfile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestCreateProfile {
    private RequestProfile profile;
    private SerchCategory category;
    private User user;
    private User referredBy;
}
