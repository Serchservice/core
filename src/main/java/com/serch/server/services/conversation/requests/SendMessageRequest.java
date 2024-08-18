package com.serch.server.services.conversation.requests;

import com.serch.server.enums.chat.MessageType;
import com.serch.server.core.storage.requests.FileUploadRequest;
import lombok.Data;

@Data
public class SendMessageRequest {
    private String room;
    private String message;
    private FileUploadRequest file;
    private MessageType type = MessageType.TEXT;
    private String duration;
    private String replied;
}