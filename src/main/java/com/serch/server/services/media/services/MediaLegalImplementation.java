package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.media.MediaLegalException;
import com.serch.server.enums.media.LegalLOB;
import com.serch.server.services.media.mappers.MediaLegalMapper;
import com.serch.server.models.media.MediaLegal;
import com.serch.server.repositories.media.MediaLegalRepository;
import com.serch.server.services.media.responses.MediaLegalGroupResponse;
import com.serch.server.services.media.responses.MediaLegalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaLegalImplementation implements MediaLegalService {
    private final MediaLegalRepository legalRepository;

    @Override
    public ApiResponse<List<MediaLegalGroupResponse>> fetchAllLegals() {
        Map<LegalLOB, List<MediaLegalResponse>> groupedByLOB = legalRepository.findAll()
                .stream()
                .map(this::getLegalResponse)
                .collect(Collectors.groupingBy(MediaLegalResponse::getLob));

        List<MediaLegalGroupResponse> response = new ArrayList<>();
        groupedByLOB.forEach((lob, legal) -> {
            MediaLegalGroupResponse legalGroupResponse = new MediaLegalGroupResponse();
            legalGroupResponse.setLineOfBusiness(lob.getType());
            legalGroupResponse.setLob(lob);
            legalGroupResponse.setLegalList(legal);
            response.add(legalGroupResponse);
        });

        return new ApiResponse<>("Legals fetched", response, HttpStatus.OK);
    }

    @Override
    public ApiResponse<MediaLegalResponse> fetchLegal(String key) {
        MediaLegal legal = legalRepository.findById(key)
                .orElseThrow(() -> new MediaLegalException("Legal Document not found"));

        return new ApiResponse<>("Legal Document fetched", getLegalResponse(legal), HttpStatus.OK);
    }

    private MediaLegalResponse getLegalResponse(MediaLegal legal) {
        MediaLegalResponse response = MediaLegalMapper.INSTANCE.response(legal);
        response.setLineOfBusiness(legal.getLob().getType());
        return response;
    }
}
