package com.serch.server.mappers;

import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.Complaint;
import com.serch.server.models.company.Issue;
import com.serch.server.models.company.ServiceSuggest;
import com.serch.server.models.company.SpeakWithSerch;
import com.serch.server.domains.category.response.SerchCategoryResponse;
import com.serch.server.domains.company.requests.ComplaintRequest;
import com.serch.server.domains.company.requests.ServiceSuggestRequest;
import com.serch.server.domains.company.responses.IssueResponse;
import com.serch.server.domains.company.responses.SpeakWithSerchResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    Complaint complaint(ComplaintRequest request);

    @Mappings({
            @Mapping(target = "sentAt", source = "createdAt"),
            @Mapping(target = "message", source = "comment")
    })
    IssueResponse response(Issue issue);

    SpeakWithSerchResponse response(SpeakWithSerch speakWithSerch);

    SerchCategoryResponse response(SerchCategory category);

    ServiceSuggest response(ServiceSuggestRequest request);
}