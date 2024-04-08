package com.serch.server.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.exceptions.account.ReferralException;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.auth.SessionException;
import com.serch.server.exceptions.media.MediaBlogException;
import com.serch.server.exceptions.media.MediaLegalException;
import com.serch.server.exceptions.media.MediaNewsroomException;
import com.serch.server.exceptions.others.*;
import com.serch.server.exceptions.subscription.PlanException;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.exceptions.transaction.WalletException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccountException.class)
    public ApiResponse<String> handleAccountException(AccountException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(ReferralException.class)
    public ApiResponse<String> handleReferralException(ReferralException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(AuthException.class)
    public ApiResponse<String> handleAuthException(AuthException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode());
        return response;
    }

    @ExceptionHandler(SessionException.class)
    public ApiResponse<String> handleSessionException(SessionException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode());
        return response;
    }

    @ExceptionHandler(EmailException.class)
    public ApiResponse<String> handleEmailException(EmailException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(HelpException.class)
    public ApiResponse<String> handleHelpException(HelpException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(MediaBlogException.class)
    public ApiResponse<String> handleMediaBlogException(MediaBlogException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(MediaLegalException.class)
    public ApiResponse<String> handleMediaLegalException(MediaLegalException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(MediaNewsroomException.class)
    public ApiResponse<String> handleMediaNewsroomException(MediaNewsroomException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(WalletException.class)
    public ApiResponse<String> handleWalletException(WalletException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(SubscriptionException.class)
    public ApiResponse<String> handleSubscriptionException(SubscriptionException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(PlanException.class)
    public ApiResponse<String> handlePlanException(PlanException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(PaymentException.class)
    public ApiResponse<String> handlePaymentException(PaymentException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(SerchException.class)
    public ApiResponse<String> handleSerchException(SerchException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(BookmarkException.class)
    public ApiResponse<String> handleBookmarkException(BookmarkException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(ShopException.class)
    public ApiResponse<String> handleShopException(ShopException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException exception) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>("Error in validating input");
        Map<String, Object> data = new HashMap<>();
        AtomicInteger count = new AtomicInteger(1);
        for(var reason : exception.getConstraintViolations()) {
            count.getAndIncrement();
            data.put("Reason %s".formatted(count), reason.getMessage());
        }
        response.setData(data);
        return response;
    }

    @ExceptionHandler(DisabledException.class)
    public ApiResponse<String> handleDisabledException(DisabledException exception) {
        return new ApiResponse<>(
                exception.getMessage(),
                ExceptionCodes.ACCOUNT_DISABLED,
                HttpStatus.LOCKED
        );
    }

    @ExceptionHandler(LockedException.class)
    public ApiResponse<String> handleLockedException(LockedException exception) {
        return new ApiResponse<>(
                exception.getMessage(),
                ExceptionCodes.ACCOUNT_LOCKED,
                HttpStatus.LOCKED
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<String> handleBadCredentialsException() {
        return new ApiResponse<>("Incorrect user details");
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ApiResponse<String> handleSocketTimeoutException() {
        return new ApiResponse<>("No network connection. Check your internet.");
    }

    @ExceptionHandler(UnknownHostException.class)
    public ApiResponse<String> handleUnknownHostException() {
        return new ApiResponse<>("No network connection. Check your internet.");
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ApiResponse<String> handleUnexpectedTypeException(UnexpectedTypeException exception) {
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<String> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        ApiResponse<String> response;
        if(exception.getMessage().contains("Detail:")) {
            int detail = exception.getMessage().indexOf("Detail:");
            int stop = exception.getMessage().indexOf(".]");
            response = new ApiResponse<>(exception.getMessage().substring(detail, stop));
        } else {
            response = new ApiResponse<>(exception.getMessage());
        }
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ApiResponse<String> handleServerException(){
        return new ApiResponse<>(
                "Invalid user input.",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConnectException.class)
    public ApiResponse<String> handleConnectException(){
        return new ApiResponse<>(
                "Connection timed out. Please check your internet connection",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MessagingException.class)
    public ApiResponse<String> handleMessagingException(MessagingException exception){
        return new ApiResponse<>(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ApiResponse<String> handleJsonProcessingException(JsonProcessingException exception) {
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<String> handleUsernameNotFoundException(UsernameNotFoundException exception){
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ApiResponse<String> handleHttpClientErrorException(HttpClientErrorException exception){
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(QrGenerationException.class)
    public ApiResponse<String> handleQrException(QrGenerationException exception){
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(WriterException.class)
    public ApiResponse<String> handleWriterException(WriterException exception){
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ApiResponse<String> handleIOException(IOException exception){
        return new ApiResponse<>(exception.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ApiResponse<String> handleExpiredJwtException(){
        return new ApiResponse<>("Token is expired. Try login or request for another");
    }

    @ExceptionHandler(SignatureException.class)
    public ApiResponse<String> handleSignatureException(){
        return new ApiResponse<>("Error processing user token");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ApiResponse<String> handleUnsupportedJwtException(){
        return new ApiResponse<>("Error reading user token");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ApiResponse<String> handleMalformedJwtException(){
        return new ApiResponse<>("Incorrect token");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request
    ) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        ApiResponse<List<String>> response = new ApiResponse<>("Validation error");
        response.setData(errors);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, WebRequest request
    ) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage());
        response.setData(request.getContextPath());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgumentException(){
        return new ApiResponse<>(
                "Invalid data format",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }
}