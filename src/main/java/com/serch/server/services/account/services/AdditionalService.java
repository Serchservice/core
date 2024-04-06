package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.incomplete.Incomplete;

public interface AdditionalService {
    void saveIncompleteAdditional(Incomplete incomplete, ApiResponse<Profile> response);
}
