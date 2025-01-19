package com.serch.server.admin.mappers;

import com.serch.server.admin.services.scopes.marketing.responses.NewsletterResponse;
import com.serch.server.admin.services.scopes.support.responses.ComplaintResponse;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchScopeResponse;
import com.serch.server.models.company.Complaint;
import com.serch.server.models.company.Newsletter;
import com.serch.server.models.company.SpeakWithSerch;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminCompanyMapper {
    AdminCompanyMapper instance = Mappers.getMapper(AdminCompanyMapper.class);

    NewsletterResponse response(Newsletter newsletter);

    ComplaintResponse response(Complaint complaint);

    SpeakWithSerchScopeResponse response(SpeakWithSerch speakWithSerch);
}