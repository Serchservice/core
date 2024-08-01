package com.serch.server.mappers;

import com.serch.server.models.account.*;
import com.serch.server.models.auth.incomplete.IncompletePhoneInformation;
import com.serch.server.models.auth.incomplete.IncompleteProfile;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.responses.*;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.requests.RequestProviderProfile;
import com.serch.server.services.account.responses.BusinessInformationData;
import com.serch.server.services.account.responses.BusinessProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    Profile profile(RequestProfile profile);
    @Mapping(source = "id", target = "id", ignore = true)
    BusinessProfile profile(IncompleteProfile profile);
    PhoneInformation phoneInformation(RequestPhoneInformation phoneInformation);
    RequestPhoneInformation phoneInformation(PhoneInformation phoneInformation);
    @Mapping(source = "id", target = "id", ignore = true)
    PhoneInformation phoneInformation(IncompletePhoneInformation phoneInformation);
    RequestProviderProfile profile(AddAssociateRequest request);
    AdditionalInformationResponse additional(AdditionalInformation additionalInformation);
    @Mappings({
            @Mapping(target = "category", source = "category", ignore = true),
            @Mapping(target = "id", source = "id")
    })
    ProfileResponse profile(Profile profile);
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "category", source = "category", ignore = true),
            @Mapping(target = "name", source = "businessName"),
            @Mapping(target = "description", source = "businessDescription"),
            @Mapping(target = "address", source = "businessAddress"),
            @Mapping(target = "logo", source = "businessLogo"),
            @Mapping(target = "avatar", source = "avatar")
    })
    BusinessProfileResponse profile(BusinessProfile profile);
    @Mappings({
            @Mapping(target = "name", source = "businessName"),
            @Mapping(target = "description", source = "businessDescription"),
            @Mapping(target = "address", source = "businessAddress"),
            @Mapping(target = "logo", source = "businessLogo"),
    })
    BusinessInformationData business(BusinessProfile profile);
    AccountSettingResponse response(AccountSetting setting);
}
