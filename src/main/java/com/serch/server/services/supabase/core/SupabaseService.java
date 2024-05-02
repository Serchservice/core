package com.serch.server.services.supabase.core;

import com.serch.server.services.supabase.responses.Country;

import java.util.List;

public interface SupabaseService {
    /**
     * Get the list of countries from database
     *
     * @return List of {@link Country}
     *
     * @see Country
     */
    List<Country> getCountries();
}
