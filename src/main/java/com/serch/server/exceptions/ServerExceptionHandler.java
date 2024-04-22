package com.serch.server.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.exceptions.others.ReferralException;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.auth.SessionException;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.exceptions.conversation.ChatException;
import com.serch.server.exceptions.conversation.ChatRoomException;
import com.serch.server.exceptions.media.MediaAssetException;
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

/**
 * The ServerExceptionHandler class handles exceptions globally for the server.
 * It extends the ResponseEntityExceptionHandler class provided by Spring using {@link RestControllerAdvice}.
 * It provides exception handling methods for various custom exceptions and standard exceptions.
 * <p></p>
 * Exceptions handled here are:
 * <ul>
 *     <li>{@link ServerExceptionHandler#handleChatException(ChatException)} -
 *     Handles exception involved with chat services. {@link ChatException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleChatRoomException(ChatRoomException)} -
 *     Handles exception involved with chat room services. {@link ChatRoomException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleCallException(CallException)} -
 *     Handles exception involved with call services. {@link CallException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleRatingException(RatingException)} -
 *     Handles exception involved with rating services. {@link RatingException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleScheduleException(ScheduleException)} -
 *     Handles exception involved with schedule services. {@link ScheduleException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleAccountException(AccountException)} -
 *     Handles exception involved with account services. {@link AccountException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleReferralException(ReferralException)} -
 *     Handles exception involved with referral services. {@link ReferralException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleAuthException(AuthException)} -
 *     Handles exception involved with authentication services. {@link AuthException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSessionException(SessionException)} -
 *     Handles exception involved with session services. {@link SessionException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleEmailException(EmailException)} -
 *     Handles exception involved with email services. {@link EmailException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleCountryException(CompanyException)} -
 *     Handles exception involved with country services. {@link CompanyException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleHelpException(HelpException)} -
 *     Handles exception involved with help services. {@link HelpException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMediaBlogException(MediaBlogException)} -
 *     Handles exception involved with media blog services. {@link MediaBlogException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMediaLegalException(MediaLegalException)} -
 *     Handles exception involved with media legal services. {@link MediaLegalException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMediaNewsroomException(MediaNewsroomException)} -
 *     Handles exception involved with media newsroom services. {@link MediaNewsroomException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleWalletException(WalletException)} -
 *     Handles exception involved with wallet services. {@link WalletException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSubscriptionException(SubscriptionException)} -
 *     Handles exception involved with subscription services. {@link SubscriptionException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handlePlanException(PlanException)} -
 *     Handles exception involved with plan services. {@link PlanException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handlePaymentException(PaymentException)} -
 *     Handles exception involved with payment services. {@link PaymentException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSerchException(SerchException)} -
 *     Handles exception involved with Serch services. {@link SerchException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleBookmarkException(BookmarkException)} -
 *     Handles exception involved with bookmark services. {@link BookmarkException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleShopException(ShopException)} -
 *     Handles exception involved with shop services. {@link ShopException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleStorageException(StorageException)} -
 *     Handles exception involved with storage services. {@link StorageException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSharedException(SharedException)} -
 *     Handles exception involved with shared services. {@link SharedException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleConstraintViolationException(ConstraintViolationException)} -
 *     Handles exception related to validation errors. {@link ConstraintViolationException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleDisabledException(DisabledException)} -
 *     Handles exception related to disabled accounts. {@link DisabledException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleLockedException(LockedException)} -
 *     Handles exception related to locked accounts. {@link LockedException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleBadCredentialsException()} -
 *     Handles exception related to bad credentials.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSocketTimeoutException()} -
 *     Handles exception related to socket timeouts.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUnknownHostException()} -
 *     Handles exception related to unknown hosts.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUnexpectedTypeException(UnexpectedTypeException)} -
 *     Handles exception related to unexpected types. {@link UnexpectedTypeException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleDataIntegrityViolationException(DataIntegrityViolationException)} -
 *     Handles exception related to data integrity violation. {@link DataIntegrityViolationException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleServerException()} -
 *     Handles exception related to server errors.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleConnectException()} -
 *     Handles exception related to connection errors.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMessagingException(MessagingException)} -
 *     Handles exception related to messaging errors. {@link MessagingException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleJsonProcessingException(JsonProcessingException)} -
 *     Handles exception related to JSON processing errors. {@link JsonProcessingException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUsernameNotFoundException(UsernameNotFoundException)} -
 *     Handles exception related to username not found. {@link UsernameNotFoundException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleHttpClientErrorException(HttpClientErrorException)} -
 *     Handles exception related to HTTP client errors. {@link HttpClientErrorException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleQrException(QrGenerationException)} -
 *     Handles exception related to QR code generation errors. {@link QrGenerationException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleWriterException(WriterException)} -
 *     Handles exception related to writer errors. {@link WriterException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleIOException(IOException)} -
 *     Handles exception related to I/O errors. {@link IOException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleExpiredJwtException()} -
 *     Handles exception related to expired JWT tokens.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSignatureException()} -
 *     Handles exception related to JWT token signature errors.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUnsupportedJwtException()} -
 *     Handles exception related to unsupported JWT tokens.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMalformedJwtException()} -
 *     Handles exception related to malformed JWT tokens.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMethodArgumentNotValid(MethodArgumentNotValidException, HttpHeaders, HttpStatusCode, WebRequest)} -
 *     Handles exception related to method argument validation errors. {@link MethodArgumentNotValidException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleHttpMessageNotReadable(HttpMessageNotReadableException, HttpHeaders, HttpStatusCode, WebRequest)} -
 *     Handles exception related to HTTP message not readable. {@link HttpMessageNotReadableException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleIllegalArgumentException()} -
 *     Handles exception related to illegal arguments.
 *     </li>
 * </ul>
 * <p></p>
 * @see ResponseEntityExceptionHandler
 * @see ApiResponse
 */
@RestControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ChatException.class)
    public ApiResponse<String> handleChatException(ChatException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(ChatRoomException.class)
    public ApiResponse<String> handleChatRoomException(ChatRoomException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(CallException.class)
    public ApiResponse<String> handleCallException(CallException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(RatingException.class)
    public ApiResponse<String> handleRatingException(RatingException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(ScheduleException.class)
    public ApiResponse<String> handleScheduleException(ScheduleException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

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

    @ExceptionHandler(CompanyException.class)
    public ApiResponse<String> handleCountryException(CompanyException exception) {
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

    @ExceptionHandler(MediaAssetException.class)
    public ApiResponse<String> handleMediaAssetException(MediaAssetException exception) {
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

    @ExceptionHandler(StorageException.class)
    public ApiResponse<String> handleStorageException(StorageException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(SharedException.class)
    public ApiResponse<String> handleSharedException(SharedException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode());
        return response;
    }

    @ExceptionHandler(TripException.class)
    public ApiResponse<String> handleTripException(TripException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }

    @ExceptionHandler(MapException.class)
    public ApiResponse<String> handleMapException(MapException exception) {
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