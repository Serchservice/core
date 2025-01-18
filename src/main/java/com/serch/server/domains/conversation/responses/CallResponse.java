package com.serch.server.domains.conversation.responses;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CallResponse {
    private CallMemberData member;
    private CallInformation recent;
    private List<CallInformation> history;
}
