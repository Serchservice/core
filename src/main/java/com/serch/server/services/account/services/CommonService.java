package com.serch.server.services.account.services;

import com.serch.server.models.account.Profile;
import com.serch.server.services.account.responses.ProfileSpecialtyResponse;

import java.util.List;

public interface CommonService {
    List<ProfileSpecialtyResponse> specialties(Profile profile);
}
