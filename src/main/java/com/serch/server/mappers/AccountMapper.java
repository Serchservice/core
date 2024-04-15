package com.serch.server.mappers;

import com.serch.server.models.account.AdditionalInformation;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.incomplete.IncompletePhoneInformation;
import com.serch.server.models.auth.incomplete.IncompleteProfile;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.responses.AdditionalInformationResponse;
import com.serch.server.services.account.responses.BusinessProfileResponse;
import com.serch.server.services.account.responses.ProfileResponse;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.requests.RequestProviderProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(source = "referral", target = "referralCode", ignore = true)
    Profile profile(RequestProfile profile);
    BusinessProfile profile(IncompleteProfile profile);
    PhoneInformation phoneInformation(RequestPhoneInformation phoneInformation);
    PhoneInformation phoneInformation(IncompletePhoneInformation phoneInformation);
    RequestProviderProfile profile(AddAssociateRequest request);
    AdditionalInformationResponse additional(AdditionalInformation additionalInformation);
    @Mappings({
            @Mapping(target = "certificate", source = "certificate", ignore = true),
            @Mapping(target = "id", source = "serchId")
    })
    ProfileResponse profile(Profile profile);
    @Mappings({
            @Mapping(target = "id", source = "serchId"),
            @Mapping(target = "name", source = "businessName"),
            @Mapping(target = "description", source = "businessDescription"),
            @Mapping(target = "address", source = "businessAddress")
    })
    BusinessProfileResponse profile(BusinessProfile profile);
}
