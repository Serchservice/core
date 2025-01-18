package com.serch.server.domains.certificate.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.bases.BaseProfile;
import com.serch.server.core.jwt.JwtService;
import com.serch.server.core.qr_code.QRCodeService;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.core.storage.requests.FileUploadRequest;
import com.serch.server.enums.account.Gender;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.exceptions.others.CertificateException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.certificate.Certificate;
import com.serch.server.repositories.account.AccountReportRepository;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.certificate.CertificateRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.domains.certificate.responses.CertificateData;
import com.serch.server.domains.certificate.responses.CertificateResponse;
import com.serch.server.domains.certificate.responses.VerifyCertificateResponse;
import com.serch.server.domains.rating.services.RatingService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificateImplementation implements CertificateService {
    private final JwtService jwtService;
    private final StorageService storageService;
    private final RatingService ratingService;
    private final QRCodeService qrCodeService;
    private final UserUtil userUtil;
    private final PasswordEncoder encoder;
    private final CertificateRepository certificateRepository;
    private final RatingRepository ratingRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final TripRepository tripRepository;
    private final AccountReportRepository accountReportRepository;

    @Value("${application.certificate.min.count}")
    private Integer CERTIFICATE_MIN_COUNT;

    @Value("${application.certificate.min.days}")
    private Integer CERTIFICATE_MIN_DAYS;

    private String date(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public ApiResponse<CertificateData> generate() {
        User user = userUtil.getUser();
        Optional<Certificate> cert = certificateRepository.findByUser(user.getId());
        if(cert.isPresent()) {
            if(instruction(user, cert.get()).values().stream().allMatch(val -> val)) {
                String secret = secret(user);
                String code = qrCodeService.platformCertificate(secret);

                cert.get().setSecret(encoder.encode(secret));
                cert.get().setCode(code);
                cert.get().setUpdatedAt(TimeUtil.now());
                cert.ifPresent(certificateRepository::save);

                return new ApiResponse<>(getCertificateData(user, code, cert.get(), cert.get().getUpdatedAt()));
            } else {
                throw new CertificateException("You cannot generate a new certificate");
            }
        } else if(instruction(user, null).values().stream().allMatch(val -> val)) {
            String secret = secret(user);
            Certificate certificate = generate(user, secret);

            return new ApiResponse<>(getCertificateData(user, certificate.getCode(), certificate, certificate.getCreatedAt()));
        } else {
            throw new CertificateException("You have not met all required conditions");
        }
    }

    private CertificateData getCertificateData(User user, String code, Certificate cert, ZonedDateTime issuedDate) {
        CertificateData data = new CertificateData();
        data.setHeader(generateHeader());
        data.setContent(generateComment(user.getFirstName()));
        data.setQrCode(code);
        data.setId(cert.getId());
        data.setName(user.getFullName());
        data.setDocument(storageService.buildUrl("/storage/v1/object/public/certificate/Unsigned.png"));
        data.setSignature(storageService.buildUrl("/storage/v1/object/public/certificate/ceo-sign.png"));
        data.setIssueDate(date(issuedDate));

        return data;
    }

    @Override
    public ApiResponse<CertificateResponse> fetch() {
        Optional<Certificate> cert = certificateRepository.findByUser(userUtil.getUser().getId());
        CertificateResponse response = new CertificateResponse();
        CertificateData data = new CertificateData();

        if(cert.isPresent()) {
            data.setDocument(cert.get().getDocument());
            response.setIsGenerated(true);
            response.setReason(
                    "Congratulations! Certificate re-generation instructions have been updated. " +
                            "You generated this current certificate on %s, so instructions are being "
                                    .formatted(date(cert.get().getUpdatedAt())) +
                            "validated from the day after your certificate was generated.\n\n" +
                            "This simply enables Serch to update your certificate content based on the " +
                            "additional information added by users of your service"
            );
            response.setInstructions(instruction(userUtil.getUser(), cert.get()));
        } else {
            data.setDocument(storageService.buildUrl(HelperUtil.dummyCertificate));
            response.setIsGenerated(false);
            response.setReason(
                    "Inorder to generate a skill certificate, you need to fulfill the instructions below. " +
                            "This enables us to generate certificate content that is personalized for you " +
                            "based on what users of your service said about you"
            );
            response.setInstructions(instruction(userUtil.getUser(), null));
        }
        response.setData(data);

        return new ApiResponse<>(response);
    }

    private boolean isThirtyDaysAfter(ZonedDateTime createdAt) {
        // Check if the current date and time is 30 days or more after the creation date
        return TimeUtil.now().isAfter(createdAt.plusDays(CERTIFICATE_MIN_DAYS)) || TimeUtil.now().isEqual(createdAt.plusDays(CERTIFICATE_MIN_DAYS));
    }

    private Map<String, Boolean> instruction(User user, Certificate certificate) {
        Map<String, Boolean> instruction = new HashMap<>();
        if(certificate != null) {
            instruction.put(
                    String.format("Generate a new certificate after %s days of current generated certificate", CERTIFICATE_MIN_DAYS),
                    isThirtyDaysAfter(certificate.getCreatedAt())
            );
            instruction.put(
                    "Run your account without being reported by any user",
                    accountReportRepository.findByAccount(String.valueOf(user.getId())).isEmpty()
                            && accountReportRepository.findByAccount(String.valueOf(user.getId())).stream().noneMatch(report -> report.getStatus() == IssueStatus.RESOLVED)
            );
        } else {
            instruction.put(
                    String.format("Engage the least of %s service trips", CERTIFICATE_MIN_COUNT),
                    tripRepository.findByProviderId(user.getId()).size() > CERTIFICATE_MIN_COUNT
            );
            instruction.put(
                    String.format("Have the least of %s rates from your service trips", CERTIFICATE_MIN_COUNT),
                    ratingRepository.findByRated(String.valueOf(user.getId())).size() > CERTIFICATE_MIN_COUNT
            );
            instruction.put(
                    "Run your account without being reported by any user",
                    !accountReportRepository.findByAccount(String.valueOf(user.getId())).isEmpty()
                    && accountReportRepository.findByAccount(String.valueOf(user.getId())).stream().noneMatch(report -> report.getStatus() == IssueStatus.RESOLVED)
            );
        }
        return instruction;
    }

    @Override
    public ApiResponse<CertificateResponse> upload(FileUploadRequest request) {
        String bucket = userUtil.getUser().isBusiness() ? "certificate/business" : "certificate/provider";
        String url = storageService.upload(request, bucket);

        Certificate certificate = certificateRepository.findByUser(userUtil.getUser().getId())
                .orElseThrow(() -> new CertificateException("Certificate not found"));
        certificate.setDocument(url);
        certificate.setUpdatedAt(TimeUtil.now());
        certificateRepository.save(certificate);

        return fetch();
    }

    private Certificate generate(User user, String secret) {
        Certificate certificate = new Certificate();
        certificate.setUser(user.getId());
        certificate.setSecret(encoder.encode(secret));
        certificate.setCode(qrCodeService.platformCertificate(secret));

        return certificateRepository.save(certificate);
    }

    private String secret(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("status", user.getStatus().name());
        claims.put("first_name", user.getFirstName());
        claims.put("last_name", user.getLastName());
        claims.put("id", user.getId());

        return jwtService.generateToken(claims, user.getEmailAddress());
    }

    private String generateHeader() {
        Double rating = ratingRepository.getOverallAverageRating(String.valueOf(userUtil.getUser().getId()));
        if(rating == 5) {
            return "your outstanding contributions, unparalleled expertise and distinctive qualities";
        } else if(rating >= 4) {
            return "your exceptional skills and dedication to excellence in your field";
        } else if(rating >= 3) {
            return "your exemplary service and professionalism to your work";
        } else if(rating >= 2) {
            return "your continuous improvement and dedication to your work";
        } else {
            return "your dedication and commitment to your work and improvement";
        }
    }

    private String gender(Gender gender) {
        return switch (gender) {
            case MALE -> "his";
            case FEMALE -> "her";
            default -> "your";
        };
    }

    private String generateComment(String firstName) {
        Double rating = ratingRepository.getOverallAverageRating(String.valueOf(userUtil.getUser().getId()));
        Gender gender = profileRepository.findById(userUtil.getUser().getId())
                .map(BaseProfile::getGender)
                .orElse(
                        businessProfileRepository.findById(userUtil.getUser().getId())
                                .map(BaseProfile::getGender)
                                .orElse(Gender.NONE)
                );

        if(rating == 5) {
            return String.format(
                    """
                            for %s exceptional talent and dedication to %s craft, presenting nothing but the best,
                            it is a honor to have %s on our Serch platform
                            and we wish %s much continued success in the future.""",
                    gender(gender), gender(gender), firstName, firstName
            );
        } else if(rating >= 4) {
            return String.format(
                    """
                            for %s unwavering commitment to providing top-notch service and ensuring customer satisfaction,
                            it is a pleasure to have %s on our Serch platform
                            and we wish %s much continued growth in the future.""",
                    gender(gender), firstName, firstName
            );
        } else if(rating >= 3) {
            return String.format(
                    """
                            for %s consistently delivering high-quality service and exceeding expectations,
                            it is a nice experience to have %s on our Serch platform
                            and we wish %s much continued growth in the future.""",
                    gender(gender), firstName, firstName
            );
        } else if(rating >= 2) {
            return String.format(
                    """
                            for %s outstanding effort in making sure that %s service rendition skills are good,
                            it is a pleasure to have %s on our Serch platform
                            and we wish %s much continued growth in the future.""",
                    gender(gender), gender(gender), firstName, firstName
            );
        } else {
            return String.format(
                    """
                            for %s earnest efforts and willingness to learn and grow in %s field,
                            it is a pleasure to have %s on our Serch platform
                            and we wish %s much continued growth in the future.""",
                    gender(gender), gender(gender), firstName, firstName
            );
        }
    }

    @Override
    public ApiResponse<VerifyCertificateResponse> verify(String token) {
        Certificate certificate = null;
        List<Certificate> certificates = certificateRepository.findAll();

        for (Certificate cert : certificates) {
            if (encoder.matches(token, cert.getSecret()) && jwtService.isTokenIssuedBySerch(token)) {
                certificate = cert;
                break;
            }
        }

        if(certificate != null) {
            String avatar = profileRepository.findById(certificate.getUser())
                    .map(BaseProfile::getAvatar)
                    .orElse(
                            businessProfileRepository.findById(certificate.getUser())
                                    .map(BaseProfile::getAvatar)
                                    .orElse("")
                    );
            String name = profileRepository.findById(certificate.getUser())
                    .map(Profile::getFullName)
                    .orElse(
                            businessProfileRepository.findById(certificate.getUser())
                                    .map(BusinessProfile::getBusinessName)
                                    .orElse("")
                    );
            SerchCategory category = profileRepository.findById(certificate.getUser())
                    .map(Profile::getCategory)
                    .orElse(
                            businessProfileRepository.findById(certificate.getUser())
                                    .map(BusinessProfile::getCategory)
                                    .orElse(SerchCategory.BUSINESS)
                    );

            VerifyCertificateResponse response = new VerifyCertificateResponse();
            response.setPicture(avatar);
            response.setDocument(certificate.getDocument());
            response.setRatings(ratingService.good(String.valueOf(certificate.getUser()), null, null).getData());

            CertificateData data = new CertificateData();
            data.setQrCode(certificate.getCode());
            data.setId(certificate.getId());
            data.setName(name);
            data.setCategory(category.getType());
            data.setImage(category.getImage());
            data.setSignature(storageService.buildUrl("/storage/v1/object/public/certificate/ceo-sign.png"));
            data.setIssueDate(date(certificate.getUpdatedAt()));
            response.setData(data);

            return new ApiResponse<>(response);
        } else {
            throw new CertificateException("Certificate does not exist");
        }
    }
}