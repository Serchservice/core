package com.serch.server.core.file.services.implementations;

import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.core.file.services.FileService;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.models.auth.User;
import com.serch.server.domains.nearby.models.go.GoBCap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileImplementation implements FileService {
    private final FileUploadWrapper service;

    @Override
    public FileUploadResponse uploadCommon(FileUploadRequest file, User user) {
        return service.uploadAndPut(file, user.getRole().getType(), user).getResponse();
    }

    @Override
    public FileUploadResponse uploadCertificate(FileUploadRequest file, User user) {
        return service.uploadAndPut(file, "/uploadCertificate/%s".formatted(user.getRole().getType()), user).getResponse();
    }

    @Override
    public FileUploadResponse guest(FileUploadRequest file, String id) {
        return service.upload(file, "/guest/%s".formatted(id), id, "guest").getResponse();
    }

    @Override
    public FileUploadResponse uploadTrip(FileUploadRequest file, String id) {
        return service.upload(file, "/trip/%s".formatted(id), id, "trip").getResponse();
    }

    @Override
    public FileUploadResponse uploadShop(FileUploadRequest file, String id) {
        return service.upload(file, "/shop/%s".formatted(id), id, "shop").getResponse();
    }

    @Override
    public FileUploadResponse uploadGo(FileUploadRequest file, UUID id) {
        return service.uploadAndPut(file, "/go/%s".formatted(id), id.toString(), "nearby").getResponse();
    }

    @Override
    public FileUploadResponse uploadGo(FileUploadRequest file, GoActivity event) {
        return service.uploadAndPut(file, "/go/activity/%s".formatted(event.getId()), event).getResponse();
    }

    @Override
    public void delete(String id) {
        service.delete(id);
    }

    @Override
    public FileUploadResponse uploadGo(FileUploadRequest file, GoBCap cap) {
        return service.uploadAndPut(file, "/go/bcap/%s".formatted(cap.getId()), cap).getResponse();
    }
}