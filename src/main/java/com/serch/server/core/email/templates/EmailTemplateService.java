package com.serch.server.core.email.templates;

/**
 * Service interface for generating email templates for various actions within the application.
 */
public interface EmailTemplateService {

    /**
     * Generates an invitation email for an admin.
     *
     * @param inviteeName the name of the person being invited
     * @param inviterName the name of the person sending the invitation
     * @param inviteLink  the link for the invitee to accept the invitation
     * @return a formatted invitation email string
     */
    String adminInvite(String inviteeName, String inviterName, String inviteLink);

    /**
     * Generates a signup email template with a verification code.
     *
     * @param code the verification code to be included in the email
     * @return a formatted signup email string
     */
    String signup(String code);

    /**
     * Generates a login verification email for an admin.
     *
     * @param name the name of the admin attempting to log in
     * @param code the verification code to confirm the login request
     * @return a formatted login verification email string
     */
    String adminLogin(String name, String code);

    /**
     * Generates a signup verification email for an admin.
     *
     * @param name the name of the admin signing up
     * @param code the verification code to confirm the signup
     * @return a formatted signup verification email string
     */
    String adminSignup(String name, String code);

    /**
     * Generates a password reset email for an admin.
     *
     * @param name      the name of the admin requesting the password reset
     * @param resetLink the link to reset the password
     * @return a formatted password reset email string
     */
    String adminResetPassword(String name, String resetLink);

    /**
     * Generates a password reset email for an admin.
     *
     * @param code the code to reset the password
     * @return a formatted password reset email string
     */
    String resetPassword(String code);

    /**
     * Generates an invitation email for an associate within a business organization.
     *
     * @param name                 the name of the associate being invited
     * @param businessName         the name of the business organization
     * @param businessAdminName    the name of the business administrator sending the invitation
     * @param businessLogo         the logo of the business organization
     * @param businessDescription   a brief description of the business
     * @param businessCategory      the category of the business
     * @param inviteLink           the link for the associate to accept the invitation
     * @return a formatted invitation email string
     */
    String associateInvite(String name, String businessName, String businessAdminName, String businessLogo, String businessDescription, String businessCategory, String inviteLink);
}