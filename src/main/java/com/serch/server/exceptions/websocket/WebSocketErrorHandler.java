package com.serch.server.exceptions.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.ApiResponseExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Service
@RequiredArgsConstructor
public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    private final ApiResponseExceptionHandler handler;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, @NonNull Throwable exception) {
        return prepareErrorMessage(clientMessage, exception);

//        return super.handleClientMessageProcessingError(clientMessage, exception);
    }

    @Override
    public Message<byte[]> handleErrorMessageToClient(@NonNull Message<byte[]> errorMessage) {
        return prepareErrorMessage(errorMessage, null);
    }

    @SneakyThrows
    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, Throwable exception) {
        ApiResponse<?> response = exception != null
                ? handler.handle(exception)
                : new ApiResponse<>("An error has occurred", clientMessage.getPayload(), HttpStatus.BAD_REQUEST);

        String message = new ObjectMapper().writeValueAsString(response);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(response.getMessage());
        accessor.setLeaveMutable(true);

        StompHeaderAccessor clientHeaderAccessor;
        if (clientMessage != null) {
            clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
            if (clientHeaderAccessor != null) {
                String receiptId = clientHeaderAccessor.getReceipt();
                if (receiptId != null) {
                    accessor.setReceiptId(receiptId);
                }
            }
        }

        return MessageBuilder.createMessage(message != null ? message.getBytes() : EMPTY_PAYLOAD, accessor.getMessageHeaders());
    }
}