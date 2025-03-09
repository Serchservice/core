package com.serch.server.core.file.services.implementations;

import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.core.file.requests.UploadRequest;
import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.core.file.services.FileService;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.models.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileImplementation implements FileService {
    private final FileUploadWrapper service;

    @Override
    public FileUploadResponse uploadCommon(FileUploadRequest file, User user) {
        UploadRequest request = new UploadRequest();
        request.setFolder(user.getRole().getType());
        request.setId(user.getId().toString());
        request.setType(String.format("%s-avatar", user.getRole().getType()));
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public FileUploadResponse uploadCertificate(FileUploadRequest file, User user) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/certificate/%s".formatted(user.getRole().getType()));
        request.setId(user.getId().toString());
        request.setType("certificate");
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public FileUploadResponse guest(FileUploadRequest file, String id) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/guest/%s".formatted(id));
        request.setId(id);
        request.setType("guest");
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public FileUploadResponse uploadTrip(FileUploadRequest file, String id) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/trip/%s".formatted(id));
        request.setId(id);
        request.setType("trip");
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public FileUploadResponse uploadShop(FileUploadRequest file, String id) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/shop/%s".formatted(id));
        request.setId(id);
        request.setType("shop");
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public FileUploadResponse uploadGo(FileUploadRequest file, UUID id) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/go/%s".formatted(id));
        request.setId(id.toString());
        request.setType("go");
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public FileUploadResponse uploadGo(FileUploadRequest file, GoActivity event) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/go-activity/%s".formatted(event.getId()));
        request.setId(event.getId());
        request.setType("go-activity");
        request.setUpload(file);

        return service.upload(request);
    }

    @Override
    public void delete(String id) {
        service.delete(id);
    }

    @Override
    public FileUploadResponse uploadGo(FileUploadRequest file, GoBCap cap) {
        UploadRequest request = new UploadRequest();
        request.setFolder("/go-bcap/%s".formatted(cap.getId()));
        request.setId(cap.getId());
        request.setType("go-bcap");
        request.setUpload(file);

        return service.upload(request);
    }
}