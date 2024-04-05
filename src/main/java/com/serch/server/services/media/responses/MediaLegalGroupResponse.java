package com.serch.server.services.media.responses;

import com.serch.server.services.media.LegalLOB;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaLegalGroupResponse {
    private String lineOfBusiness;
    private LegalLOB lob;
    private List<MediaLegalResponse> legalList;
}
