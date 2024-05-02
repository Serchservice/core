package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.company.SpecialtyKeyword;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.company.SpecialtyKeywordRepository;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service class for managing specialties and specialty keywords.
 * It implements its wrapper class {@link SpecialtyService}
 *
 * @see SpecialtyRepository
 * @see SpecialtyKeywordRepository
 * @see SpecialtyKeywordService
 * @see ProfileRepository
 * @see UserUtil
 */
@Service
@RequiredArgsConstructor
public class SpecialtyImplementation implements SpecialtyService {
    private final SpecialtyKeywordService keywordService;
    private final UserUtil userUtil;
    private final SpecialtyRepository specialtyRepository;
    private final ProfileRepository profileRepository;
    private final SpecialtyKeywordRepository specialtyKeywordRepository;

    @Value("${application.account.limit.specialty}")
    private Integer SPECIALTY_LIMIT;

    @Override
    public void createSpecialties(Incomplete incomplete, Profile profile) {
        if(!incomplete.getSpecializations().isEmpty()) {
            incomplete.getSpecializations().forEach(specialty -> {
                Specialty special = new Specialty();
                special.setService(specialty.getService());
                special.setProfile(profile);
                specialtyRepository.save(special);
            });
        }
    }

    @Override
    public ApiResponse<SpecialtyKeywordResponse> add(Long id) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));
        SpecialtyKeyword keyword = specialtyKeywordRepository.findById(id)
                .orElseThrow(() -> new AccountException("Specialty not found"));

        int size = specialtyRepository.findByProfile_Id(userUtil.getUser().getId()).size();
        if(size < SPECIALTY_LIMIT) {
            Specialty special = new Specialty();
            special.setService(keyword);
            special.setProfile(profile);
            specialtyRepository.save(special);

            return new ApiResponse<>(
                    "Specialty added. Remaining %s".formatted(SPECIALTY_LIMIT - size),
                    keywordService.getSpecialtyResponse(keyword),
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
}
