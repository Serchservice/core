package com.serch.server.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import com.serch.server.admin.exceptions.AdminException;
import com.serch.server.admin.exceptions.PermissionException;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.auth.SessionException;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.exceptions.conversation.ChatException;
import com.serch.server.exceptions.conversation.ChatRoomException;
import com.serch.server.exceptions.media.MediaAssetException;
import com.serch.server.exceptions.media.MediaBlogException;
import com.serch.server.exceptions.media.MediaLegalException;
import com.serch.server.exceptions.media.MediaNewsroomException;
import com.serch.server.exceptions.nearby.NearbyException;
import com.serch.server.exceptions.others.*;
import com.serch.server.exceptions.transaction.WalletException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.SystemException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.JDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTimeoutException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
 *     <li>{@link ServerExceptionHandler#handleBadCredentialsException(BadCredentialsException)} ()} -
 *     Handles exception related to bad credentials.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSocketTimeoutException(SocketTimeoutException)} -
 *     Handles exception related to socket timeouts.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUnknownHostException(UnknownHostException)} -
 *     Handles exception related to unknown hosts.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUnexpectedTypeException(UnexpectedTypeException)} -
 *     Handles exception related to unexpected types. {@link UnexpectedTypeException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleDataIntegrityViolationException(DataIntegrityViolationException)} -
 *     Handles exception related to response integrity violation. {@link DataIntegrityViolationException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleHttpServerErrorException(HttpServerErrorException)} -
 *     Handles exception related to server errors.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleConnectException(ConnectException)} -
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
 *     <li>{@link ServerExceptionHandler#handleWriterException(WriterException)} -
 *     Handles exception related to writer errors. {@link WriterException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleIOException(IOException)} -
 *     Handles exception related to I/O errors. {@link IOException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleExpiredJwtException(ExpiredJwtException)} -
 *     Handles exception related to expired JWT tokens.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleSignatureException(SignatureException)} -
 *     Handles exception related to JWT token signature errors.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleUnsupportedJwtException(UnsupportedJwtException)} -
 *     Handles exception related to unsupported JWT tokens.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMalformedJwtException(MalformedJwtException)} -
 *     Handles exception related to malformed JWT tokens.
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleMethodArgumentNotValid(MethodArgumentNotValidException, HttpHeaders, HttpStatusCode, WebRequest)} -
 *     Handles exception related to method argument validation errors. {@link MethodArgumentNotValidException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleHttpMessageNotReadable(HttpMessageNotReadableException, HttpHeaders, HttpStatusCode, WebRequest)} -
 *     Handles exception related to HTTP message not readable. {@link HttpMessageNotReadableException}
 *     </li>
 *     <li>{@link ServerExceptionHandler#handleIllegalArgumentException(IllegalArgumentException)} -
 *     Handles exception related to illegal arguments.
 *     </li>
 * </ul>
 * <p></p>
 * @see ResponseEntityExceptionHandler
 * @see ApiResponse
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ServerExceptionHandler.class);

    @ExceptionHandler(AdminException.class)
    public ApiResponse<String> handleAdminException(AdminException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());


        return response;
    }

    @ExceptionHandler(VerificationException.class)
    public ApiResponse<String> handleVerificationException(VerificationException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(PermissionException.class)
    public ApiResponse<String> handlePermissionException(PermissionException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(ChatException.class)
    @MessageExceptionHandler(ChatException.class)
    public ApiResponse<String> handleChatException(ChatException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(ChatRoomException.class)
    public ApiResponse<String> handleChatRoomException(ChatRoomException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(CertificateException.class)
    public ApiResponse<String> handleCertificateException(CertificateException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(CallException.class)
    public ApiResponse<String> handleCallException(CallException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode() != null ? exception.getCode() : exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(RatingException.class)
    public ApiResponse<String> handleRatingException(RatingException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(ScheduleException.class)
    public ApiResponse<String> handleScheduleException(ScheduleException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(AccountException.class)
    public ApiResponse<String> handleAccountException(AccountException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(ReferralException.class)
    public ApiResponse<String> handleReferralException(ReferralException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(AuthException.class)
    public ApiResponse<String> handleAuthException(AuthException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(SessionException.class)
    public ApiResponse<String> handleSessionException(SessionException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(EmailException.class)
    public ApiResponse<String> handleEmailException(EmailException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(CompanyException.class)
    public ApiResponse<String> handleCountryException(CompanyException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(HelpException.class)
    public ApiResponse<String> handleHelpException(HelpException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(NearbyException.class)
    public ApiResponse<String> handleNearbyException(NearbyException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(MediaBlogException.class)
    public ApiResponse<String> handleMediaBlogException(MediaBlogException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(MediaLegalException.class)
    public ApiResponse<String> handleMediaLegalException(MediaLegalException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(MediaAssetException.class)
    public ApiResponse<String> handleMediaAssetException(MediaAssetException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(MediaNewsroomException.class)
    public ApiResponse<String> handleMediaNewsroomException(MediaNewsroomException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(WalletException.class)
    public ApiResponse<String> handleWalletException(WalletException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(PaymentException.class)
    public ApiResponse<String> handlePaymentException(PaymentException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(SerchException.class)
    public ApiResponse<String> handleSerchException(SerchException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(BookmarkException.class)
    public ApiResponse<String> handleBookmarkException(BookmarkException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(ShopException.class)
    public ApiResponse<String> handleShopException(ShopException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(LinkedDynamicException.class)
    public ResponseEntity<String> handleLinkedDynamicException(LinkedDynamicException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return ResponseEntity.badRequest().body(response.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    public ApiResponse<String> handleStorageException(StorageException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(SharedException.class)
    public ApiResponse<String> handleSharedException(SharedException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getCode());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(TripException.class)
    public ApiResponse<String> handleTripException(TripException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(MapException.class)
    public ApiResponse<String> handleMapException(MapException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error(exception.getMessage());

        int violationsCount = exception.getConstraintViolations().size();
        String message;

        if (violationsCount > 1) {
            // More than one violation, format the messages as a list
            String violations = exception.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            message = "Your request needs to comply with these violations: " + violations;
        } else if (violationsCount == 1) {
            // Only one violation, return that single message
            message = exception.getConstraintViolations().iterator().next().getMessage();
        } else {
            // No violations (this should not happen under normal circumstances)
            message = "Error in validating input";
        }

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(message);

        Map<String, Object> data = new HashMap<>();
        AtomicInteger count = new AtomicInteger(1);
        for(var reason : exception.getConstraintViolations()) {
            count.getAndIncrement();
            data.put("Reason %s".formatted(count), reason.getMessage());
        }
        response.setData(data);
        response.setStatus(HttpStatus.BAD_REQUEST);


        return response;
    }

    @ExceptionHandler(DisabledException.class)
    public ApiResponse<String> handleDisabledException(DisabledException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), ExceptionCodes.ACCOUNT_DISABLED, HttpStatus.LOCKED);
    }

    @ExceptionHandler(LockedException.class)
    public ApiResponse<String> handleLockedException(LockedException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), ExceptionCodes.ACCOUNT_LOCKED, HttpStatus.LOCKED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<String> handleBadCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>("Incorrect user details", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ApiResponse<String> handleSocketTimeoutException(SocketTimeoutException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>("No network connection. Check your internet.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownHostException.class)
    public ApiResponse<String> handleUnknownHostException(UnknownHostException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>("No network connection. Check your internet.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ApiResponse<String> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>("Request was not completed due to server issues. Try again later", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ApiResponse<String> handleUnexpectedTypeException(UnexpectedTypeException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<String> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error(exception.getMessage());

        ApiResponse<String> response;
        if(exception.getMessage().contains("Detail:")) {
            int detail = exception.getMessage().indexOf("Detail:");
            int stop = exception.getMessage().indexOf(".]");
            response = new ApiResponse<>(exception.getMessage().substring(detail, stop));
        } else {
            response = new ApiResponse<>(exception.getMessage());
        }
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setData(exception.getLocalizedMessage());

        return response;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ApiResponse<String> handleHttpServerErrorException(HttpServerErrorException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Invalid user input.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectException.class)
    public ApiResponse<String> handleConnectException(ConnectException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "Connection timed out. Please check your internet connection",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MessagingException.class)
    public ApiResponse<String> handleMessagingException(MessagingException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ApiResponse<String> handleJsonProcessingException(JsonProcessingException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ApiResponse<String> handleDateTimeParseException(DateTimeParseException exception) {
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<String> handleUsernameNotFoundException(UsernameNotFoundException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ApiResponse<String> handleHttpClientErrorException(HttpClientErrorException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeException.class)
    public ApiResponse<String> handleDateTimeException(DateTimeException exception){
        log.error(exception.getMessage());
        log.error(String.valueOf(exception.getCause()));

        return new ApiResponse<>("An error occurred while formatting your data. Try again.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WriterException.class)
    public ApiResponse<String> handleWriterException(WriterException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JDBCException.class)
    public ApiResponse<String> handleJDBCException(JDBCException exception){
        log.error(exception.getSQLException().getMessage());

        return new ApiResponse<>("An error happened while fetching response, try again.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JDBCConnectionException.class)
    public ApiResponse<String> handleJDBCConnectionException(JDBCConnectionException exception){
        log.error(exception.getSQLException().getMessage());

        return new ApiResponse<>("Couldn't complete connection while fetching response, try again", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ApiResponse<String> handleIOException(IOException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ApiResponse<String> handleExpiredJwtException(ExpiredJwtException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Token is expired. Try login or request for another", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignatureException.class)
    public ApiResponse<String> handleSignatureException(SignatureException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Error processing user token", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ApiResponse<String> handleUnsupportedJwtException(UnsupportedJwtException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Error reading user token", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ApiResponse<String> handleMalformedJwtException(MalformedJwtException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Incorrect token", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRouteToHostException.class)
    public ApiResponse<String> handleNoRouteToHostException(NoRouteToHostException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Host not found for specified route. Please check your internet", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(@NonNull NoResourceFoundException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.error(ex.getMessage());

        ApiResponse<String> response = new ApiResponse<>("Couldn't finish your request, try again later.");
        response.setData(request.getContextPath());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, response.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.error(ex.getMessage());

        ApiResponse<String> response = new ApiResponse<>("Couldn't complete your request due to an error from the client.");
        response.setData(request.getContextPath());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Couldn't complete your request due to an error from the client.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GeneralSecurityException.class)
    public ApiResponse<String> handleGeneralSecurityException(GeneralSecurityException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ApiResponse<String> handleNoSuchAlgorithmException(NoSuchAlgorithmException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("An error occurred while performing your request. Please try again", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<String> handleIllegalStateException(IllegalStateException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("An error occurred while performing your request. Please try again", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAlgorithmParameterException.class)
    public ApiResponse<String> handleInvalidAlgorithmParameterException(InvalidAlgorithmParameterException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("An error occurred while performing your request. Please try again", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    public ApiResponse<String> handleStringIndexOutOfBoundsException(StringIndexOutOfBoundsException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidKeyException.class)
    public ApiResponse<String> handleInvalidKeyException(InvalidKeyException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("An error occurred while performing your request. Try again", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request
    ) {
        int violationsCount = ex.getBindingResult().getAllErrors().size();
        String message;

        if (violationsCount > 1) {
            // More than one violation, format the messages as a list
            String violations = ex.getBindingResult().getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            message = "Your request needs to comply with these violations: " + violations;
        } else if (violationsCount == 1) {
            // Only one violation, return that single message
            message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        } else {
            // No violations (this should not happen under normal circumstances)
            message = "Error in validating input";
        }

        log.error(ex.getMessage());
        log.error(message);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(message);

        Map<String, Object> data = new HashMap<>();
        AtomicInteger count = new AtomicInteger(1);
        for(var reason : ex.getBindingResult().getAllErrors()) {
            count.getAndIncrement();
            data.put("Reason %s".formatted(count), reason.getDefaultMessage());
        }
        response.setData(data);
        response.setStatus(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, response.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage());

        ApiResponse<String> response = new ApiResponse<>(ex.getMessage());
        response.setData(request.getContextPath());
        response.setStatus(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<String> handleNullPointerException(NullPointerException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                exception.getMessage(),
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(AssertionError.class)
    public ApiResponse<String> handleAssertionError(AssertionError exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                exception.getMessage(),
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(DataSourceLookupFailureException.class)
    public ApiResponse<String> handleDataSourceLookupFailureException(DataSourceLookupFailureException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "An error occurred while fetching response, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ApiResponse<String> handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "An error occurred while fetching response, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ApiResponse<String> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "An error occurred while fetching response, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(SystemException.class)
    public ApiResponse<String> handleSystemException(SystemException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "An error occurred while fetching response, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(SQLException.class)
    public ApiResponse<String> handleSQLException(SQLException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "An error occurred while fetching response, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(SQLDataException.class)
    public ApiResponse<String> handleSQLDataException(SQLDataException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "An error occurred while fetching response, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(SQLNonTransientConnectionException.class)
    public ApiResponse<String> handleSQLNonTransientConnectionException(SQLNonTransientConnectionException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "Connection error occurred, please try again",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(SQLTimeoutException.class)
    public ApiResponse<String> handleSQLTimeoutException(SQLTimeoutException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>(
                "Timeout. Error while fetching response",
                ExceptionCodes.IMPROPER_USER_ID_FORMAT,
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ApiResponse<String> handleTransactionSystemException(TransactionSystemException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("An error occurred while saving your response. Try again", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessageDeliveryException.class)
    public ApiResponse<String> handleMessageDeliveryException(MessageDeliveryException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("An error occurred while sending your message", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ApiResponse<String> handleUnauthorized(HttpClientErrorException.Unauthorized exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Unauthorized web access", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<String> handleAccessDeniedException(AccessDeniedException exception){
        log.error(exception.getMessage());

        return new ApiResponse<>("Unauthorized web access", HttpStatus.BAD_REQUEST);
    }
}