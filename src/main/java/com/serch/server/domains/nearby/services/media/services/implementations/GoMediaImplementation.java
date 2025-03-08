package com.serch.server.domains.nearby.services.media.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.core.file.services.FileService;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.models.go.GoMedia;
import com.serch.server.domains.nearby.repositories.go.GoMediaRepository;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;
import com.serch.server.domains.nearby.services.media.services.GoMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoMediaImplementation implements GoMediaService {
    private final FileService fileService;
    private final GoMediaRepository goMediaRepository;

    @Override
    @Transactional
    public ApiResponse<Void> delete(Long id) {
        GoMedia media = goMediaRepository.findById(id).orElse(null);

        if(media != null) {
            if(media.getAssetId() != null && !media.getAssetId().isEmpty()) {
                fileService.delete(media.getAssetId());
            } else if(media.getPublicId() != null && !media.getPublicId().isEmpty()) {
                fileService.delete(media.getPublicId());
            }

            goMediaRepository.delete(media);
            return new ApiResponse<>("Media deleted successfully", HttpStatus.OK);
        } else {
            return new ApiResponse<>("Media not deleted. Couldn't locate resource file.");
        }
    }

    @Override
    public List<GoMedia> upload(GoCreateActivityRequest request, GoActivity activity) {
        return request.getImages().stream().map(upload -> {
            GoMedia media = GoMapper.instance.media(fileService.uploadGo(upload, activity));
            media.setActivity(activity);
            goMediaRepository.save(media);

            return media;
        }).toList();
    }

    @Override
    public List<GoMedia> upload(List<FileUploadRequest> files, GoBCap cap) {
        return files.stream().map(upload -> {
            GoMedia media = GoMapper.instance.media(fileService.uploadGo(upload, cap));
            media.setBcap(cap);
            goMediaRepository.save(media);

            return media;
        }).toList();
    }
}