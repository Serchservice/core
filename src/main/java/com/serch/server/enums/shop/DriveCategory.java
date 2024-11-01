package com.serch.server.enums.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The DriveCategory enum represents drive-related categories in the application,
 * including categories specific to Google Places API types.
 */
@Getter
@RequiredArgsConstructor
public enum DriveCategory {
    MECHANIC(
            "Mechanic",
            "https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/categories/mechanic.png",
            "car_repair"
    ),
    PLUMBER(
            "Plumber",
            "https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/categories/plumber.png",
            "plumber"
    ),
    ELECTRICIAN(
            "Electrician",
            "https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/categories/electrician.png",
            "electrician"
    ),
    BUSINESS(
            "Business",
            "https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/categories/business.png",
            "accounting"
    ),
    // Additional Google Place types not covered by SerchCategory
    AIRPORT("Airport", "", "airport"),
    AMUSEMENT_PARK("Amusement Park", "", "amusement_park"),
    AQUARIUM("Aquarium", "", "aquarium"),
    ART_GALLERY("Art Gallery", "", "art_gallery"),
    ATM("ATM", "", "atm"),
    BAKERY("Bakery", "", "bakery"),
    BANK("Bank", "", "bank"),
    BAR("Bar", "", "bar"),
    BEAUTY_SALON("Beauty Salon", "", "beauty_salon"),
    BICYCLE_STORE("Bicycle Store", "", "bicycle_store"),
    BOOK_STORE("Book Store", "", "book_store"),
    BOWLING_ALLEY("Bowling Alley", "", "bowling_alley"),
    BUS_STATION("Bus Station", "", "bus_station"),
    CAFE("Cafe", "", "cafe"),
    CAMPGROUND("Campground", "", "campground"),
    CAR_DEALER("Car Dealer", "", "car_dealer"),
    CAR_RENTAL("Car Rental", "", "car_rental"),
    CAR_WASH("Car Wash", "", "car_wash"),
    CASINO("Casino", "", "casino"),
    CEMETERY("Cemetery", "", "cemetery"),
    CHURCH("Church", "", "church"),
    CITY_HALL("City Hall", "", "city_hall"),
    CLOTHING_STORE("Clothing Store", "", "clothing_store"),
    CONVENIENCE_STORE("Convenience Store", "", "convenience_store"),
    COURTHOUSE("Courthouse", "", "courthouse"),
    DENTIST("Dentist", "", "dentist"),
    DEPARTMENT_STORE("Department Store", "", "department_store"),
    DOCTOR("Doctor", "", "doctor"),
    DRUGSTORE("Drugstore", "", "drugstore"),
    ELECTRONICS_STORE("Electronics Store", "", "electronics_store"),
    EMBASSY("Embassy", "", "embassy"),
    FIRE_STATION("Fire Station", "", "fire_station"),
    FLORIST("Florist", "", "florist"),
    FUNERAL_HOME("Funeral Home", "", "funeral_home"),
    FURNITURE_STORE("Furniture Store", "", "furniture_store"),
    GAS_STATION("Gas Station", "", "gas_station"),
    GYM("Gym", "", "gym"),
    HAIR_CARE("Hair Care", "", "hair_care"),
    HARDWARE_STORE("Hardware Store", "", "hardware_store"),
    HINDU_TEMPLE("Hindu Temple", "", "hindu_temple"),
    HOME_GOODS_STORE("Home Goods Store", "", "home_goods_store"),
    HOSPITAL("Hospital", "", "hospital"),
    INSURANCE_AGENCY("Insurance Agency", "", "insurance_agency"),
    JEWELRY_STORE("Jewelry Store", "", "jewelry_store"),
    LAUNDRY("Laundry", "", "laundry"),
    LAWYER("Lawyer", "", "lawyer"),
    LIBRARY("Library", "", "library"),
    LIGHT_RAIL_STATION("Light Rail Station", "", "light_rail_station"),
    LIQUOR_STORE("Liquor Store", "", "liquor_store"),
    LOCAL_GOVERNMENT_OFFICE("Local Government Office", "", "local_government_office"),
    LOCKSMITH("Locksmith", "", "locksmith"),
    LODGING("Lodging", "", "lodging"),
    MEAL_DELIVERY("Meal Delivery", "", "meal_delivery"),
    MEAL_TAKEAWAY("Meal Takeaway", "", "meal_takeaway"),
    MOSQUE("Mosque", "", "mosque"),
    MOVIE_RENTAL("Movie Rental", "", "movie_rental"),
    MOVIE_THEATER("Movie Theater", "", "movie_theater"),
    MOVING_COMPANY("Moving Company", "", "moving_company"),
    MUSEUM("Museum", "", "museum"),
    NIGHT_CLUB("Night Club", "", "night_club"),
    PAINTER("Painter", "", "painter"),
    PARK("Park", "", "park"),
    PARKING("Parking", "", "parking"),
    PET_STORE("Pet Store", "", "pet_store"),
    PHARMACY("Pharmacy", "", "pharmacy"),
    PHYSIOTHERAPIST("Physiotherapist", "", "physiotherapist"),
    POLICE("Police", "", "police"),
    POST_OFFICE("Post Office", "", "post_office"),
    PRIMARY_SCHOOL("Primary School", "", "primary_school"),
    REAL_ESTATE_AGENCY("Real Estate Agency", "", "real_estate_agency"),
    RESTAURANT("Restaurant", "", "restaurant"),
    ROOFING_CONTRACTOR("Roofing Contractor", "", "roofing_contractor"),
    RV_PARK("RV Park", "", "rv_park"),
    SCHOOL("School", "", "school"),
    SECONDARY_SCHOOL("Secondary School", "", "secondary_school"),
    SHOE_STORE("Shoe Store", "", "shoe_store"),
    SHOPPING_MALL("Shopping Mall", "", "shopping_mall"),
    SPA("Spa", "", "spa"),
    STADIUM("Stadium", "", "stadium"),
    STORAGE("Storage", "", "storage"),
    STORE("Store", "", "store"),
    SUBWAY_STATION("Subway Station", "", "subway_station"),
    SUPERMARKET("Supermarket", "", "supermarket"),
    SYNAGOGUE("Synagogue", "", "synagogue"),
    TAXI_STAND("Taxi Stand", "", "taxi_stand"),
    TOURIST_ATTRACTION("Tourist Attraction", "", "tourist_attraction"),
    TRAIN_STATION("Train Station", "", "train_station"),
    TRANSIT_STATION("Transit Station", "", "transit_station"),
    TRAVEL_AGENCY("Travel Agency", "", "travel_agency"),
    UNIVERSITY("University", "", "university"),
    VETERINARY_CARE("Veterinary Care", "", "veterinary_care"),
    ZOO("Zoo", "", "zoo");

    private final String type;
    private final String image;
    private final String google;

    public static DriveCategory get(String type) {
        // Check if the type matches the enum name (case-sensitive)
        try {
            return DriveCategory.valueOf(type);
        } catch (IllegalArgumentException e) {
            // Enum name not found, so proceed to check fields

            // Check if the type matches the `type` or `google` field
            for (DriveCategory category : DriveCategory.values()) {
                if (category.getType().equalsIgnoreCase(type) || category.getGoogle().equalsIgnoreCase(type)) {
                    return category;
                }
            }
        }

        return null;
    }
}
