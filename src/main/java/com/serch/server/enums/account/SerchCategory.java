package com.serch.server.enums.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The SerchCategory enum represents different categories in the Serch application.
 * Each enum constant corresponds to a specific category and provides a descriptive type.
 * <p></p>
 * This enum is annotated with {@link lombok.Lombok}, {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum SerchCategory {
    MECHANIC(
            "Mechanic",
            true,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/mechanic.png"
    ),
    PLUMBER(
            "Plumber",
            true,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/plumber.png"
    ),
    ELECTRICIAN(
            "Electrician",
            true,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/electrician.png"
    ),
    BUSINESS(
            "Business",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/business.png"
    ),
    PERSONAL_SHOPPER(
            "Personal Shopper",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/shopper.png"
    ),
    HOUSE_KEEPING(
            "House Keeper",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/housekeeper.png"
    ),
    CARPENTER(
            "Carpenter",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/carpenter.png"
    ),
    GUEST(
            "Guest",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/guest.png"
    ),
    USER(
            "User",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/user.png"
    );

    private final String type;
    private final boolean shouldSubscribe;
    private final String image;
}