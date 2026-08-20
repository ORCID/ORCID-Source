package org.orcid.frontend.web.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The single password reset entry we keep in redis per record, holding the latest issued token and
 * whether it has already been used to reset the password.
 *
 * Generating a new link overwrites the entry, which deactivates any previously issued link.
 */
public class PasswordResetTokenEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetTokenEntry.class);

    private static final String KEY_PREFIX = "password-reset-token-";

    private static final String TOKEN_FIELD = "token";

    private static final String USED_FIELD = "used";

    private final String token;

    private final boolean used;

    public PasswordResetTokenEntry(String token, boolean used) {
        this.token = token;
        this.used = used;
    }

    public static String redisKey(String orcid) {
        return KEY_PREFIX + orcid;
    }

    public String getToken() {
        return token;
    }

    public boolean isUsed() {
        return used;
    }

    public String serialize() {
        try {
            JSONObject json = new JSONObject();
            json.put(TOKEN_FIELD, token);
            json.put(USED_FIELD, used);
            return json.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to serialize password reset token entry", e);
        }
    }

    /**
     * @param value
     *            the raw redis value, or null when there is no entry for the record
     * @return the parsed entry, or null when there is nothing usable stored
     */
    public static PasswordResetTokenEntry parse(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(value);
            return new PasswordResetTokenEntry(json.getString(TOKEN_FIELD), json.getBoolean(USED_FIELD));
        } catch (JSONException e) {
            LOGGER.error("Unable to parse the password reset token entry, treating it as missing", e);
            return null;
        }
    }
}
