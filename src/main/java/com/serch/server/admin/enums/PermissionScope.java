package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionScope {
    USER("User", "User Management", "Allows access to user-related functionalities, including managing user profiles, preferences, and account settings."),
    PROVIDER("Provider", "Service Provider Management", "Grants access to provider-specific functionalities, such as managing service offerings, schedules, and availability."),
    ASSOCIATE_PROVIDER("Associate Provider", "Associate Provider Access", "Allows limited access to provider-related tasks, assisting in service management and coordination."),
    BUSINESS("Business", "Business Operations", "Enables access to business management functionalities like company details, analytics, and operational settings."),
    PAYMENT("Payment", "Payment Processing", "Grants permissions for payment-related tasks, such as managing transactions, invoices, and refunds."),
    SUPPORT("Support", "Customer Support", "Allows access to support-related tasks, including managing support tickets, customer queries, and communication."),
    PRODUCT("Product", "Product Management", "Enables management of products or services, such as catalog updates, inventory, and product details."),
    ADMIN("Admin", "Administrative Access", "Grants full access to all system functionalities, including managing users, roles, settings, and configurations."),
    GUEST("Guest", "Guest Access", "Provides limited access to basic functionalities, suitable for unauthenticated or temporary users.");

    private final String scope;
    private final String title;
    private final String description;
}