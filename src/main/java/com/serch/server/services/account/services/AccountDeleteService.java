package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;

import java.util.UUID;

/**
 * This interface holds the methods that handle the deletion of any Serch account.
 * There are several options when it comes to deletion of account in Serch which includes but not limited to:
 * <ul>
 *     <li>Associate Provider Account Deletion - Governed by the Business Account Administrator
 *      which has the account as its Provider in the Serch platform {@link AccountDeleteService#delete(UUID)}.
 *      The business account administrator has to pass the id of the associate provider for the request to
 *      go through.
 *     </li>
 *     <li>Account Deletion (Serch User, Serch Provider, Serch Business) - This is the owner of an
 *     individual Serch account {@link AccountDeleteService#delete()}
 *     </li>
 * </ul>
 * <p></p>
 * Implementation of this interface can be seen here {@link com.serch.server.services.account.services.implementations.AccountDeleteImplementation}
 */
public interface AccountDeleteService {
    /**
     * @param id Associate Provider id which belongs to the logged-in business account
     * <p></p>
     * This method is called for business accounts in Serch - Serch Business, to request for account
     * deletion of any Serch Associate Provider account belonging to the business.
     * @return ApiResponse of String
     * @see ApiResponse
     */
    ApiResponse<String> delete(UUID id);

    /**
     * This method is called for either User or Provider accounts.
     * The method uses the logged-in user
     * details to work on its deletion request.
     * @return ApiResponse of String
     * @see ApiResponse
     */
    ApiResponse<String> delete();

    /**
     * Delete/Remove the saved details of the user - Used for creating accounts
     *
     * @param emailAddress Email Address of the user
     */
    void undo(String emailAddress);
}
