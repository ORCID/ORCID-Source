package org.orcid.utils.sms;

public class SmsSendResult {

    private final boolean success;
    private final String provider;
    private final String providerMessageId;
    private final String status;
    private final String errorCode;
    private final String errorMessage;

    private SmsSendResult(boolean success, String provider, String providerMessageId, String status, String errorCode, String errorMessage) {
        this.success = success;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static SmsSendResult success(String provider, String providerMessageId, String status) {
        return new SmsSendResult(true, provider, providerMessageId, status, null, null);
    }

    public static SmsSendResult failure(String provider, String errorCode, String errorMessage) {
        return new SmsSendResult(false, provider, null, "FAILED", errorCode, errorMessage);
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

    public String getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
