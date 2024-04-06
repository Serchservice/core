package com.serch.server.mappers;

//import com.serch.server.models.account.PhoneInformation;
//import com.serch.server.models.account.Profile;
//import com.serch.server.services.auth.requests.RequestPhoneInformation;
//import com.serch.server.services.auth.requests.RequestProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

//    @Mapping(source = "referral", target = "referralCode", ignore = true)
//    Profile profile(RequestProfile profile);
//    PhoneInformation phoneInformation(RequestPhoneInformation phoneInformation);
}
