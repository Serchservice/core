package com.serch.server.mappers;

import com.serch.server.models.company.Complaint;
import com.serch.server.models.company.Issue;
import com.serch.server.models.company.SpeakWithSerch;
import com.serch.server.models.company.Team;
import com.serch.server.services.company.requests.ComplaintRequest;
import com.serch.server.services.company.responses.IssueResponse;
import com.serch.server.services.company.responses.SpeakWithSerchResponse;
import com.serch.server.services.company.responses.TeamResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    TeamResponse response(Team team);
    Complaint complaint(ComplaintRequest request);
    @Mappings({
            @Mapping(target = "sentAt", source = "createdAt"),
            @Mapping(target = "message", source = "comment")
    })
    IssueResponse response(Issue issue);
    SpeakWithSerchResponse response(SpeakWithSerch speakWithSerch);
}