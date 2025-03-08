package com.serch.server.domains.nearby.services.bcap.services;

import com.serch.server.domains.nearby.models.go.GoBCap;

/**
 * This interface defines a callback for handling the creation of a GoBCap.
 * <p></p>
 * Implementations of this interface can be used to trigger specific actions
 * or perform side effects when a new GoBCap is created.
 * <p></p>
 * For example, sending notifications to related users,
 * updating associated data in other systems,
 * or performing other business logic related to GoBCap creation.
 */
public interface GoBCapLifecycleService {
    /**
     * Callback method invoked when a new GoBCap is created.
     *
     * @param cap The newly created GoBCap object.
     */
    void onCreated(GoBCap cap);
}