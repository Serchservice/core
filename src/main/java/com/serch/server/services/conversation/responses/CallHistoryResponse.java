package com.serch.server.services.conversation.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CallHistoryResponse extends CallResponse {
    private CallTimeInfo time;
    private CallMemberData member;
}
