package com.serch.server.services.media.responses;

import com.serch.server.services.media.enums.LegalLineOfBusiness;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaLegalResponse {
    private String legal;
    private String image;
    private String key;
    private String lineOfBusiness;
    private LegalLineOfBusiness lob;
    private String title;
}
