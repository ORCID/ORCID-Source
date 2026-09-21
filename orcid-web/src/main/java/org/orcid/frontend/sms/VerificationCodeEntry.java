package org.orcid.frontend.sms;

/**
 * An issued verification code awaiting confirmation. ORCID is the authoritative store of the code; the managed provider
 * only delivered it.
 */
public class VerificationCodeEntry {

    private final String code;
    private final String provider;
    private final String providerMessageId;
    private final long expiresAtEpochMs;
    private int attempts;

    public VerificationCodeEntry(String code, String provider, String providerMessageId, long expiresAtEpochMs) {
        this.code = code;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.expiresAtEpochMs = expiresAtEpochMs;
        this.attempts = 0;
    }

    public String getCode() {
        return code;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public boolean isExpired(long nowEpochMs) {
        return nowEpochMs >= expiresAtEpochMs;
    }

    public int getAttempts() {
        return attempts;
    }

    public int incrementAttempts() {
        return ++attempts;
    }
}
