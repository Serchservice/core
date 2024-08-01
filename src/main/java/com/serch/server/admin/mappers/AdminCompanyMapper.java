package com.serch.server.admin.mappers;

import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddCountryRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountryInSerchResponse;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.StateInSerchResponse;
import com.serch.server.admin.services.scopes.marketing.responses.NewsletterResponse;
import com.serch.server.admin.services.scopes.support.responses.ComplaintResponse;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchScopeResponse;
import com.serch.server.models.company.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminCompanyMapper {
    AdminCompanyMapper instance = Mappers.getMapper(AdminCompanyMapper.class);

    NewsletterResponse response(Newsletter newsletter);
    ComplaintResponse response(Complaint complaint);
    SpeakWithSerchScopeResponse response(SpeakWithSerch speakWithSerch);

    @Mapping(target = "country", source = "name")
    CountryInSerchResponse response(RequestedCountry country);

    @Mapping(target = "country", source = "name")
    CountryInSerchResponse response(LaunchedCountry country);

    @Mapping(target = "state", source = "name")
    StateInSerchResponse response(RequestedState state);

    @Mapping(target = "state", source = "name")
    StateInSerchResponse response(LaunchedState state);

    @Mapping(target = "name", source = "country")
    LaunchedCountry country(AddCountryRequest request);
}