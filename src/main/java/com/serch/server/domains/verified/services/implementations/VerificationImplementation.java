package com.serch.server.domains.verified.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.verified.services.VerificationService;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.exceptions.others.VerificationException;
import com.serch.server.mappers.VerifiedMapper;
import com.serch.server.models.auth.verified.Verification;
import com.serch.server.repositories.auth.verified.VerificationRepository;
import com.serch.server.domains.verified.responses.VerificationResponse;
import com.serch.server.domains.verified.responses.VerificationStage;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.serch.server.enums.verified.VerificationType.CONSENT;

@Service
@RequiredArgsConstructor
public class VerificationImplementation implements VerificationService {
    private static final Logger log = LoggerFactory.getLogger(VerificationImplementation.class);

    private final AuthUtil authUtil;
    private final VerificationRepository verificationRepository;

    @Value("${application.verification.expiration.time.link}")
    private Integer VERIFICATION_LINK_EXPIRATION_TIME;

    @Value("${application.verification.expiration.time.wait}")
    private Integer VERIFICATION_WAIT_EXPIRATION_TIME;

    @Override
    public ApiResponse<VerificationResponse> verification() {
        return new ApiResponse<>(buildResponse(authUtil.getUser().getId()));
    }

    @Override
    public VerificationResponse buildResponse(UUID userId) {
        Verification verification = verificationRepository.findById(userId).orElse(null);

        VerificationResponse response;
        if(verification == null) {
            response = new VerificationResponse();
            response.setMessage("Kickstart your verification process swiftly");
            response.setStatus(VerificationStatus.NOT_VERIFIED);
            response.setConsent(buildNoConsent());
            response.setLink("");
            response.setRemaining("");
        } else {
            response = VerifiedMapper.INSTANCE.response(verification);
            if(verification.isVerified()) {
                response.setMessage("Your verification is complete. You are good to go!");
                response.setRemaining(TimeUtil.formatDay(verification.getUpdatedAt(), verification.getUser().getTimezone()));
                response.setLink("");
            } else {
                response.setMessage("Complete your verification, this is vital");

                if(verification.getLink() == null) {
                    response.setLink("");
                    response.setRemaining(getRemainingWaitTime(verification.getCreatedAt().toLocalDateTime()));
                } else {
                    response.setRemaining(getRemainingLinkTime(verification.getUpdatedAt().toLocalDateTime()));
                }
            }
            response.setConsent(buildConsent(verification));
        }

        log.debug(String.format("VERIFICATION RESPONSE::: ID=%s | Data=%s", userId, response));

        return response;
    }

    private VerificationStage buildNoConsent() {
        VerificationStage stage = new VerificationStage();
        stage.setName(CONSENT.getName());
        stage.setType(CONSENT.name());
        stage.setPending(false);
        stage.setVerified(false);
        return stage;
    }

    private VerificationStage buildConsent(Verification verification) {
        VerificationStage stage = new VerificationStage();
        stage.setName(CONSENT.getName());
        stage.setType(CONSENT.name());
        stage.setPending(verification.hasConsent());
        stage.setVerified(verification.hasConsent());
        return stage;
    }

    private String getRemainingWaitTime(LocalDateTime createdAt) {
        LocalDateTime expirationTime = createdAt.plusHours(VERIFICATION_WAIT_EXPIRATION_TIME);
        System.out.println(expirationTime);
        return formatDuration(Duration.between(LocalDateTime.now(), expirationTime), "Ready for verification", "Ready");
    }

    private String getRemainingLinkTime(LocalDateTime updatedAt) {
        LocalDateTime expirationTime = updatedAt.plusDays(VERIFICATION_LINK_EXPIRATION_TIME);
        return formatDuration(Duration.between(LocalDateTime.now(), expirationTime), "Expired", "Expires");
    }

    private String formatDuration(Duration duration, String finisher, String append) {
        long seconds = duration.getSeconds();
        if (seconds <= 0) {
            return finisher;
        }

        long days = seconds / (24 * 3600);
        seconds %= 24 * 3600;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder(append + " ");

        if (days > 0) {
            if (days == 1) {
                sb.append("tomorrow");
            } else if (days < 30) {
                sb.append("in ").append(days).append(" days");
            } else {
                sb.append("in ").append(days / 30).append(" month");
                long remainingDays = days % 30;
                if (remainingDays > 0) {
                    sb.append(" and ").append(remainingDays).append(" days");
                }
            }
        } else if (hours > 0) {
            sb.append("in ").append(hours).append(" hours");
        } else if (minutes > 0) {
            sb.append("in ").append(minutes).append(" minutes");
        } else {
            sb.append("in ").append(seconds).append(" seconds");
        }

        return sb.toString().trim();
    }

    @Override
    public ApiResponse<VerificationResponse> consent() {
        Verification verification = verificationRepository.findById(authUtil.getUser().getId()).orElse(null);
        if(verification == null) {
            Verification newVerification = new Verification();
            newVerification.setUser(authUtil.getUser());
            newVerification.setMannerConsent(ConsentType.YES);
            newVerification.setLiabilityConsent(ConsentType.YES);
            newVerification.setRegulationConsent(ConsentType.YES);
            newVerification.setUseOfDataConsent(ConsentType.YES);
            newVerification.setTripConsent(ConsentType.YES);
            newVerification.setStatus(VerificationStatus.REQUESTED);
            verificationRepository.save(newVerification);
        } else if(verification.hasConsent()) {
            throw new VerificationException("You have given consent already");
        } else {
            verification.setMannerConsent(ConsentType.YES);
            verification.setLiabilityConsent(ConsentType.YES);
            verification.setRegulationConsent(ConsentType.YES);
            verification.setUseOfDataConsent(ConsentType.YES);
            verification.setTripConsent(ConsentType.YES);
            verification.setStatus(VerificationStatus.REQUESTED);
            verificationRepository.save(verification);
        }
        return new ApiResponse<>(buildResponse(authUtil.getUser().getId()));
    }
}