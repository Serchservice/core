package com.serch.server.services.conversation.responses;

import com.serch.server.enums.account.SerchCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class CallMemberData {
    private UUID member;
    private String name;
    private String avatar;
    private SerchCategory category;
}
