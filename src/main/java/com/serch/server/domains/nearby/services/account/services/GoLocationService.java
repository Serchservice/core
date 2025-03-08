package com.serch.server.domains.nearby.services.account.services;

import com.serch.server.core.location.responses.Address;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.services.account.responses.GoLocationResponse;

/**
 * This interface defines the contract for managing user location.
 * It provides methods for updating user location.
 */
public interface GoLocationService {
    /**
     * Updates the user's location.
     *
     * @param user The {@link GoUser} object representing the user.
     * @param address The {@link Address} object representing the user's new location.
     */
    void put(GoUser user, Address address);

    /**
     * Updates the user's location.
     *
     * @param activity The {@link GoActivity} object representing the activity.
     * @param response The {@link GoLocationResponse} object containing the activity's location details.
     */
    void put(GoActivity activity, GoLocationResponse response);
}