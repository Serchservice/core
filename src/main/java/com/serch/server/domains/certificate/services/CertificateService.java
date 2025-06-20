package com.serch.server.domains.certificate.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.certificate.responses.CertificateData;
import com.serch.server.domains.certificate.responses.CertificateResponse;
import com.serch.server.domains.certificate.responses.VerifyCertificateResponse;
import com.serch.server.core.file.requests.FileUploadRequest;

/**
 * This is the wrapper class that oversees the generation and verification of certificates issued in Serch
 */
public interface CertificateService {
    /**
     * This generates a uploadCertificate for a provider/business. If the uploadCertificate has been generated,
     * it will generate a new one if the timeframe for the generated one has passed, or it will
     * get the existing one.
     *
     * @return {@link ApiResponse} of {@link CertificateData}
     */
    ApiResponse<CertificateData> generate();

    /**
     * This fetches the uploadCertificate of the provider or business
     *
     * @return {@link ApiResponse} of {@link CertificateResponse}
     */
    ApiResponse<CertificateResponse> fetch();

    /**
     * This uploads the uploadCertificate of the provider or business
     *
     * @param request The Upload request
     * @return {@link ApiResponse} of {@link CertificateResponse}
     */
    ApiResponse<CertificateResponse> upload(FileUploadRequest request);

    /**
     * This verifies a generated uploadCertificate in the Serch platform.
     *
     * @param token The secret token that is unique to each uploadCertificate generated.
     *
     * @return {@link ApiResponse} of {@link VerifyCertificateResponse}
     */
    ApiResponse<VerifyCertificateResponse> verify(String token);
}