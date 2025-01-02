package com.serch.server.exceptions;

/**
 * The ExceptionCodes class defines static constants for various exception codes used in the application.
 * These codes are used to identify specific types of exceptions or errors.
 * <p></p>
 * The codes include:
 * <ul>
 *     <li>{@link ExceptionCodes#EXPIRED_SESSION} - Code for expired session error.</li>
 *     <li>{@link ExceptionCodes#ACCOUNT_LOCKED} - Code for locked account error.</li>
 *     <li>{@link ExceptionCodes#ACCOUNT_DISABLED} - Code for disabled account error.</li>
 *     <li>{@link ExceptionCodes#INCORRECT_TOKEN} - Code for incorrect token error.</li>
 *     <li>{@link ExceptionCodes#IMPROPER_USER_ID_FORMAT} - Code for improper user ID format error.</li>
 *     <li>{@link ExceptionCodes#USER_NOT_FOUND} - Code for user not found error.</li>
 *     <li>{@link ExceptionCodes#ACCESS_DENIED} - Code for access denied error.</li>
 *     <li>{@link ExceptionCodes#INCORRECT_LOGIN} - Code for incorrect login error.</li>
 *     <li>{@link ExceptionCodes#ASSOCIATE_PROVIDER_EMAIL} - Code for associate provider email error.</li>
 *     <li>{@link ExceptionCodes#EMAIL_NOT_VERIFIED} - Code for email not verified error.</li>
 *     <li>{@link ExceptionCodes#PROFILE_NOT_SET} - Code for profile not set error.</li>
 *     <li>{@link ExceptionCodes#ACCOUNT_NOT_CREATED} - Code for additional details not set error.</li>
 *     <li>{@link ExceptionCodes#CATEGORY_NOT_SET} - Code for category not set error.</li>
 *     <li>{@link ExceptionCodes#EXISTING_USER} - Code for existing user error.</li>
 *     <li>{@link ExceptionCodes#UNKNOWN} - Code for unknown error.</li>
 *     <li>{@link ExceptionCodes#CALL_ERROR} - Code for call error.</li>
 * </ul>
 */
public class ExceptionCodes {
    public static final String EXPIRED_SESSION = "S10";
    public static final String ACCOUNT_LOCKED = "S11";
    public static final String ACCOUNT_DISABLED = "S12";
    public static final String INCORRECT_TOKEN = "S20";
    public static final String IMPROPER_USER_ID_FORMAT = "S30";
    public static final String USER_NOT_FOUND = "S40";
    public static final String ACCESS_DENIED = "S50";
    public static final String INCORRECT_LOGIN = "S60";
    public static final String ASSOCIATE_PROVIDER_EMAIL = "S70";
    public static final String EMAIL_NOT_VERIFIED = "S80";
    public static final String PROFILE_NOT_SET = "S90";
    public static final String ACCOUNT_NOT_CREATED = "S92";
    public static final String CATEGORY_NOT_SET = "S96";
    public static final String EXISTING_USER = "S100";
    public static final String UNKNOWN = "S400";
    public static final String CALL_ERROR = "S65";
    public static final String MFA_COMPULSORY = "S423";
    public static final String ON_TRIP = "S111";
    public static final String INCOMPLETE_REGISTRATION = "S900";
}