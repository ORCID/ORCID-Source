package org.orcid.frontend.sms;

public class SmsPocResponse {

    private boolean success;
    private String provider;
    private String providerMessageId;
    private String normalizedPhoneNumber;
    private String status;
    private Boolean verified;
    private String errorCode;
    private String errorMessage;

    public static SmsPocResponse success(String provider, String providerMessageId, String normalizedPhoneNumber, String status) {
        SmsPocResponse response = new SmsPocResponse();
        response.success = true;
        response.provider = provider;
        response.providerMessageId = providerMessageId;
        response.normalizedPhoneNumber = normalizedPhoneNumber;
        response.status = status;
        return response;
    }

    /**
     * Result of a verification check: {@code success} means the request was processed; {@code verified} carries whether
     * the supplied code matched.
     */
    public static SmsPocResponse verificationResult(String provider, String providerMessageId, String normalizedPhoneNumber,
            boolean verified, String status) {
        SmsPocResponse response = new SmsPocResponse();
        response.success = true;
        response.provider = provider;
        response.providerMessageId = providerMessageId;
        response.normalizedPhoneNumber = normalizedPhoneNumber;
        response.verified = verified;
        response.status = status;
        return response;
    }

    public static SmsPocResponse failure(String provider, String normalizedPhoneNumber, String errorCode, String errorMessage) {
        SmsPocResponse response = new SmsPocResponse();
        response.success = false;
        response.provider = provider;
        response.normalizedPhoneNumber = normalizedPhoneNumber;
        response.status = "FAILED";
        response.errorCode = errorCode;
        response.errorMessage = errorMessage;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getNormalizedPhoneNumber() {
        return normalizedPhoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getVerified() {
        return verified;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
