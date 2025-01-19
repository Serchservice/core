package com.serch.server.admin.services.organization.services.implementations;

import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.Organization;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.repositories.OrganizationRepository;
import com.serch.server.admin.services.organization.data.OrganizationDto;
import com.serch.server.admin.services.organization.data.OrganizationResponse;
import com.serch.server.admin.services.organization.services.OrganizationService;
import com.serch.server.admin.services.scopes.common.services.CommonProfileService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.qr_code.QRCodeService;
import com.serch.server.core.token.TokenService;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationImplementation implements OrganizationService {
    private final QRCodeService qrCodeService;
    private final CommonProfileService profileService;
    private final TokenService tokenService;
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;
    private final UserUtil userUtil;

    @Override
    public ApiResponse<List<OrganizationResponse>> getAllOrganizations() {
        List<OrganizationResponse> organizations = organizationRepository.findAll()
                .stream()
                .map(org -> {
                    OrganizationResponse response = AdminMapper.instance.response(org);
                    response.setAdmin(profileService.fromUser(org.getAdmin().getUser()));
                    response.setQrCode(String.format("response:image/png;base64,%s", qrCodeService.generateAdminDetails(org.getSecret())));

                    return response;
                }).toList();

        return new ApiResponse<>(organizations);
    }

    @Override
    public ApiResponse<List<OrganizationResponse>> add(OrganizationDto organization) {
        Admin admin = adminRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new SerchException("No admin found"));

        if(organizationRepository.findByUsernameIgnoreCaseOrEmailAddressIgnoreCase(organization.username(), organization.emailAddress()).isPresent()) {
            throw new SerchException("Username or email address already exists");
        }

        Organization data = AdminMapper.instance.toEntity(organization);
        data.setSecret(tokenService.generate(12));
        data.setAdmin(admin);
        organizationRepository.save(data);

        return getAllOrganizations();
    }

    @Override
    @Transactional
    public ApiResponse<List<OrganizationResponse>> delete(Long id) {
        organizationRepository.deleteById(id);
        organizationRepository.flush();

        return getAllOrganizations();
    }

    @Override
    public ApiResponse<List<OrganizationResponse>> update(OrganizationDto organization, Long id) {
        adminRepository.findById(userUtil.getUser().getId()).orElseThrow(() -> new SerchException("No admin found"));
        Organization existing = organizationRepository.findById(id)
                .orElseThrow(() -> new SerchException("Organization member not found"));

        existing = AdminMapper.instance.partialUpdate(organization, existing);
        organizationRepository.save(existing);

        return getAllOrganizations();
    }

    @Override
    public ApiResponse<OrganizationDto> getOrganization(String secret) {
        Organization existing = organizationRepository.findBySecret(secret)
                .orElseThrow(() -> new SerchException("Organization member not found"));

        return new ApiResponse<>(AdminMapper.instance.toDto(existing));
    }
}