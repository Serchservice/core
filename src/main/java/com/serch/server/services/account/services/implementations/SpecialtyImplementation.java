package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.account.responses.SpecialtyResponse;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing specialties and specialty keywords.
 * It implements its wrapper class {@link SpecialtyService}
 *
 * @see SpecialtyRepository
 * @see ProfileRepository
 * @see UserUtil
 */
@Service
@RequiredArgsConstructor
public class SpecialtyImplementation implements SpecialtyService {
    private final UserUtil userUtil;
    private final SpecialtyRepository specialtyRepository;
    private final ProfileRepository profileRepository;

    @Value("${application.account.limit.specialty}")
    private Integer SPECIALTY_LIMIT;

    @Override
    public void createSpecialties(Incomplete incomplete, Profile profile) {
        if(!incomplete.getSpecializations().isEmpty()) {
            incomplete.getSpecializations().forEach(specialty -> {
                Specialty special = new Specialty();
                special.setSpecialty(specialty.getSpecialty());
                special.setProfile(profile);
                specialtyRepository.save(special);
            });
        }
    }

    @Override
    public ApiResponse<SpecialtyResponse> add(String specialty) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));

        int size = specialtyRepository.findByProfile_Id(userUtil.getUser().getId()).size();
        if(size < SPECIALTY_LIMIT) {
            Specialty special = new Specialty();
            special.setSpecialty(specialty);
            special.setProfile(profile);
            specialtyRepository.save(special);

            return new ApiResponse<>(
                    "Specialty added. Remaining %s".formatted(SPECIALTY_LIMIT - size),
                    response(special),
                    HttpStatus.CREATED
            );
        } else {
            throw new AccountException("You have reached the limit of specialties you can added");
        }
    }

    @Override
    public ApiResponse<String> delete(Long id) {
        specialtyRepository.findByIdAndProfile_Id(id, userUtil.getUser().getId())
                .ifPresentOrElse(specialtyRepository::delete, () -> {
                    throw new AccountException("An error occurred while performing action");
                });
        return new ApiResponse<>("Successfully deleted", HttpStatus.OK);
    }

    @Override
    public SpecialtyResponse response(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .avatar(specialty.getProfile().getAvatar())
                .category(specialty.getProfile().getCategory().getType())
                .special(specialty.getSpecialty())
                .image(specialty.getProfile().getCategory().getImage())
                .build();
    }

    @Override
    public ApiResponse<List<SpecialtyResponse>> search(String query) {
        List<Specialty> specialties = specialtyRepository.fullTextSearch(query.toLowerCase());
        if(specialties != null && !specialties.isEmpty()) {
            return new ApiResponse<>(specialties.stream().map(this::response).toList());
        } else {
            return new ApiResponse<>(List.of());
        }
    }

    @Override
    public ApiResponse<List<String>> specialties() {
        List<String> response = specialtyRepository.findAll()
                .stream()
                .map(Specialty::getSpecialty)
                .toList();

        return new ApiResponse<>(response);
    }
}