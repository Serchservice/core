package com.serch.server.exceptions.websocket;

import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.others.SerchException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketExceptionController {

    @MessageExceptionHandler(SerchException.class)
    @SendToUser("/queue/errors")
    public String handleSerchException(SerchException ex) {
        return ex.getMessage();
    }

    @MessageExceptionHandler(AuthException.class)
    @SendToUser("/queue/errors")
    public String handleAuthException(AuthException ex) {
        return ex.getMessage();
    }
}
