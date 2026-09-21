package org.orcid.frontend.recoveryphone;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The single pending recovery phone verification code per record.
 *
 * Sending a code overwrites the entry, so a code sent to a previous number
 * stops working as soon as the user asks for a new one. The number the code was
 * sent to is kept alongside it so the save step can refuse a code that was
 * issued for a different number.
 */
public class RecoveryPhoneCodeEntry {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryPhoneCodeEntry.class);

    private static final String KEY_PREFIX = "recovery-phone-code-";

    private static final String CODE_FIELD = "code";
    private static final String PHONE_FIELD = "phone";
    private static final String PROVIDER_FIELD = "provider";
    private static final String MESSAGE_ID_FIELD = "messageId";
    private static final String ATTEMPTS_FIELD = "attempts";
    private static final String SENT_AT_FIELD = "sentAt";

    private final String code;

    private final String phoneE164;

    private final String provider;

    private final String providerMessageId;

    private int attempts;

    private final long sentAt;

    public RecoveryPhoneCodeEntry(String code, String phoneE164, String provider, String providerMessageId, int attempts, long sentAt) {
        this.code = code;
        this.phoneE164 = phoneE164;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.attempts = attempts;
        this.sentAt = sentAt;
    }

    public static String redisKey(String orcid) {
        return KEY_PREFIX + orcid;
    }

    public String getCode() {
        return code;
    }

    public String getPhoneE164() {
        return phoneE164;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public int getAttempts() {
        return attempts;
    }

    public int incrementAttempts() {
        return ++attempts;
    }

    public long getSentAt() {
        return sentAt;
    }

    public String serialize() {
        try {
            JSONObject json = new JSONObject();
            json.put(CODE_FIELD, code);
            json.put(PHONE_FIELD, phoneE164);
            json.put(PROVIDER_FIELD, provider);
            json.put(MESSAGE_ID_FIELD, providerMessageId == null ? JSONObject.NULL : providerMessageId);
            json.put(ATTEMPTS_FIELD, attempts);
            json.put(SENT_AT_FIELD, sentAt);
            return json.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to serialize recovery phone code entry", e);
        }
    }

    /**
     * @param value
     *            the raw stored value, or null when there is no pending code
     * @return the parsed entry, or null when there is nothing usable stored
     */
    public static RecoveryPhoneCodeEntry parse(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(value);
            return new RecoveryPhoneCodeEntry(json.getString(CODE_FIELD), json.getString(PHONE_FIELD), json.getString(PROVIDER_FIELD),
                    json.isNull(MESSAGE_ID_FIELD) ? null : json.getString(MESSAGE_ID_FIELD), json.getInt(ATTEMPTS_FIELD),
                    json.getLong(SENT_AT_FIELD));
        } catch (JSONException e) {
            LOG.error("Unable to parse the recovery phone code entry, treating it as missing", e);
            return null;
        }
    }

}
