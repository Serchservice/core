package com.serch.server.core.session.implementations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.session.GuestSessionService;
import com.serch.server.core.session.requests.GuestSession;
import com.serch.server.core.validator.implementations.KeyValidator;
import com.serch.server.domains.shared.responses.GuestResponse;
import com.serch.server.domains.shared.services.SharedService;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.utils.DatabaseUtil;
import com.serch.server.utils.TimeUtil;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class GuestSessionImplementation implements GuestSessionService {
    private final Gson gson = new Gson();
    private final SharedService sharedService;
    private final KeyValidator keyValidator;
    private final SharedLoginRepository sharedLoginRepository;

    @Value("${application.access.enc.key}")
    protected String ENC_SECRET_KEY;

    @Value("${application.access.enc.cipher}")
    protected String ENC_CIPHER;

    @Value("${application.access.enc.iv}")
    protected String ENC_INITIALIZATION_VECTOR;

    @Value("${application.access.signature}")
    private String ACCESS_SIGNATURE;

    private SecretKeySpec getSecretKey() {
        return new SecretKeySpec(ENC_SECRET_KEY.getBytes(), "AES");
    }

    @SneakyThrows
    private Cipher getEncryptionCipher() {
        Cipher cipher = Cipher.getInstance(ENC_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), getIv());

        return cipher;
    }

    @SneakyThrows
    private Cipher getDecryptionCipher() {
        Cipher cipher = Cipher.getInstance(ENC_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), getIv());

        return cipher;
    }


    private IvParameterSpec getIv() {
        return new IvParameterSpec(ENC_INITIALIZATION_VECTOR.getBytes());
    }

    @Override
    public GuestResponse response(SharedLogin login) {
        GuestResponse response = SharedMapper.INSTANCE.guest(login.getGuest());
        response.setGender(login.getGuest().getGender().getType());
        response.setConfirmed(login.getGuest().isEmailConfirmed());
        response.setJoinedAt(TimeUtil.formatDay(login.getCreatedAt(), ""));
        response.setSession(encode(login.getStatus(), login.getId()));

        response.setLink(sharedService.data(
                login.getSharedLink(),
                sharedService.getCurrentStatusForAccount(login)
        ));

        if(login.getStatuses() != null && !login.getStatuses().isEmpty()) {
            response.setStatuses(login.getStatuses()
                    .stream()
                    .filter(sharedStatus -> sharedStatus.getSharedLogin().getId().equals(login.getId()))
                    .sorted(Comparator.comparingInt(stat -> stat.getUseStatus().getCount()))
                    .map(status -> sharedService.getStatusData(login.getSharedLink(), status))
                    .toList()
            );
        }

        return response;
    }

    @SneakyThrows
    String encode(UseStatus status, Long id) {
        String json = gson.toJson(new GuestSession(
                id,
                status,
                Encoders.BASE64.encode(ACCESS_SIGNATURE.getBytes()),
                LocalDate.now().plusWeeks(2)
        ));

        Cipher cipher = getEncryptionCipher();
        byte[] encryptedBytes = cipher.doFinal(json.getBytes());

        return DatabaseUtil.encode(encryptedBytes);
    }

    @SneakyThrows
    GuestSession decode(String token) {
        byte[] decodedBytes = DatabaseUtil.decode(token);

        Cipher cipher = getDecryptionCipher();
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        String json = new String(decryptedBytes);
        return gson.fromJson(json, new TypeToken<GuestSession>() {}.getType());
    }

    @Override
    public ApiResponse<String> validateSession(String token) {
        GuestSession session = decode(token);

        if (session.getExpiry().isBefore(LocalDate.now())) {
            throw new AuthException("Session has expired");
        }

        if(keyValidator.isSigned(new String(Decoders.BASE64.decode(session.getKey())))) {
            SharedLogin login = sharedLoginRepository.findById(session.getId())
                    .orElseThrow(() -> new AuthException("Invalid session details"));

            return new ApiResponse<>("Successful", String.valueOf(login.getId()), HttpStatus.OK);
        } else {
            throw new AuthException("Invalid session signature");
        }
    }
}