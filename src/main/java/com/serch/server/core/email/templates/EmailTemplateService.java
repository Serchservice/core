package com.serch.server.core.email.templates;

/**
 * Service interface for generating email templates for various actions within the application.
 * <p>
 * This interface defines methods for creating standardized email templates that can be used
 * throughout the application for various user interactions, such as invitations, signups,
 * and password resets. Implementations of this interface should ensure that the templates
 * are well-structured and formatted for clarity and effectiveness.
 * </p>
 */
public interface EmailTemplateService {

    /**
     * Generates an invitation email for an admin.
     * <p>
     * This method creates a formatted invitation email string that includes the names of
     * both the inviter and invitee, along with a link for the invitee to accept the
     * invitation. The generated email can be used to onboard new admins into the system.
     * </p>
     *
     * @param inviteeName the name of the person being invited.
     * @param inviterName the name of the person sending the invitation.
     * @param inviteLink  the link for the invitee to accept the invitation.
     * @return a formatted invitation email string.
     */
    String adminInvite(String inviteeName, String inviterName, String inviteLink);

    /**
     * Generates a signup email template with a verification code.
     * <p>
     * This method returns a formatted email string containing the verification code
     * required for the user to complete their signup process. The template should
     * encourage the user to verify their email address promptly.
     * </p>
     *
     * @param code the verification code to be included in the email.
     * @return a formatted signup email string.
     */
    String signup(String code);

    /**
     * Generates a login verification email for an admin.
     * <p>
     * This method creates a formatted email containing a verification code that
     * an admin can use to confirm their login attempt. It is essential for
     * securing admin accounts against unauthorized access.
     * </p>
     *
     * @param name the name of the admin attempting to log in.
     * @param code the verification code to confirm the login request.
     * @return a formatted login verification email string.
     */
    String adminLogin(String name, String code);

    /**
     * Generates a signup verification email for an admin.
     * <p>
     * This method produces a formatted email that includes a verification code for
     * an admin during the signup process. It assists in ensuring the validity of
     * the signup request.
     * </p>
     *
     * @param name the name of the admin signing up.
     * @param code the verification code to confirm the signup.
     * @return a formatted signup verification email string.
     */
    String adminSignup(String name, String code);

    /**
     * Generates a password reset email for an admin.
     * <p>
     * This method constructs a formatted email that provides a reset link for
     * an admin who has requested a password reset. The link should lead the
     * admin to a secure password reset interface.
     * </p>
     *
     * @param name      the name of the admin requesting the password reset.
     * @param resetLink the link to reset the password.
     * @return a formatted password reset email string.
     */
    String adminResetPassword(String name, String resetLink);

    /**
     * Generates a password reset email for a user.
     * <p>
     * This method creates a formatted email containing a reset code for users
     * wishing to reset their password. The code can be used in conjunction with
     * a reset interface to complete the password change.
     * </p>
     *
     * @param code the code to reset the password.
     * @return a formatted password reset email string.
     */
    String resetPassword(String code);

    /**
     * Generates an invitation email for an associate within a business organization.
     * <p>
     * This method constructs a formatted invitation email string that includes
     * detailed information about the business organization and the associate
     * being invited. The email provides context about the business and
     * contains a link for the associate to accept the invitation.
     * </p>
     *
     * @param name                 the name of the associate being invited.
     * @param businessName         the name of the business organization.
     * @param businessAdminName    the name of the business administrator sending the invitation.
     * @param businessLogo         the logo of the business organization.
     * @param businessDescription   a brief description of the business.
     * @param businessCategory      the category of the business.
     * @param inviteLink           the link for the associate to accept the invitation.
     * @return a formatted invitation email string.
     */
    String associateInvite(String name, String businessName, String businessAdminName,
                           String businessLogo, String businessDescription,
                           String businessCategory, String inviteLink
    );
}