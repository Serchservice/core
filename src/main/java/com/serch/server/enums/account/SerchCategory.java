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
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/mechanic.png",
            "What skills do you have",
            true,
            true
    ),
    PLUMBER(
            "Plumber",
            true,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/plumber.png",
            "What skills do you have",
            true,
            true
    ),
    ELECTRICIAN(
            "Electrician",
            true,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/electrician.png",
            "What skills do you have",
            true,
            true
    ),
    BUSINESS(
            "Business",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/business.png",
            "What skills do you have",
            true,
            true
    ),
    PERSONAL_SHOPPER(
            "Personal Shopper",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/shopper.png",
            "What skills do you have",
            false,
            false
    ),
    HOUSE_KEEPING(
            "House Keeper",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/housekeeper.png",
            "What skills do you have",
            false,
            false
    ),
    CARPENTER(
            "Carpenter",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/carpenter.png",
            "What skills do you have",
            true,
            true
    ),
    GUEST(
            "Guest",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/guest.png",
            "What skills do you have",
            true,
            true
    ),
    USER(
            "User",
            false,
            "https://wyvcjsumdfoamsmdzsna.supabase.co/storage/v1/object/public/categories/user.png",
            "What skills do you have",
            true,
            true
    );

    private final String type;
    private final Boolean shouldSubscribe;
    private final String image;
    private final String information;
    private final boolean canDrive;
    private final boolean canSearchSkill;
}