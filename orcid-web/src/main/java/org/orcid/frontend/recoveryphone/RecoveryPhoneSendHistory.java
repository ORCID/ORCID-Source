package org.orcid.frontend.recoveryphone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When verification texts were sent for a record, kept separately from the
 * pending code.
 *
 * The resend buffer used to be read off the pending code entry's own timestamp,
 * which meant every path that removes that entry - confirming the code, running
 * the attempts out, letting it expire - also removed the buffer, and the next
 * send was accepted at once. Sending is what costs money and what reaches a
 * stranger's phone, so what bounds it cannot be state that verifying is allowed
 * to delete.
 *
 * One value per record, holding the times texts actually went out. Older entries
 * are pruned on read, so the list is bounded by the window rather than by how
 * long the record has existed.
 */
public class RecoveryPhoneSendHistory {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveryPhoneSendHistory.class);

    private static final String KEY_PREFIX = "recovery-phone-sends-";

    private static final String SENDS_FIELD = "sends";

    private final List<Long> sends;

    public RecoveryPhoneSendHistory() {
        this(new ArrayList<Long>());
    }

    private RecoveryPhoneSendHistory(List<Long> sends) {
        this.sends = sends;
    }

    public static String redisKey(String orcid) {
        return KEY_PREFIX + orcid;
    }

    /**
     * Records a text that the provider accepted. Only successful sends are
     * recorded: a send the provider refused reached nobody and cost nothing, so
     * holding it against the record would make an outage look like abuse.
     */
    public void recordSend(long at) {
        sends.add(at);
        Collections.sort(sends);
    }

    /**
     * @return when the last text went out, or 0 when none has within the window
     */
    public long lastSentAt() {
        return sends.isEmpty() ? 0L : sends.get(sends.size() - 1);
    }

    public boolean isEmpty() {
        return sends.isEmpty();
    }

    /**
     * Drops everything older than the window, so a record that sent a code last
     * week starts today with a clean history.
     */
    public void prune(long now, long windowMillis) {
        sends.removeIf(sentAt -> sentAt <= now - windowMillis);
    }

    public String serialize() {
        try {
            JSONObject json = new JSONObject();
            json.put(SENDS_FIELD, new JSONArray(sends));
            return json.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to serialize the recovery phone send history", e);
        }
    }

    /**
     * @param value
     *            the raw stored value, blank when the record has no history
     * @return the parsed history, an empty one when nothing is stored, and null
     *         when something is stored that cannot be read
     *
     *         The pending code entry treats an unreadable value as no value,
     *         which costs a user one code. Doing that here would hand whoever
     *         wrote the unreadable value a fresh allowance, so an unreadable
     *         history is reported as such and the caller refuses the send.
     */
    public static RecoveryPhoneSendHistory parse(String value) {
        if (StringUtils.isBlank(value)) {
            return new RecoveryPhoneSendHistory();
        }
        try {
            JSONObject json = new JSONObject(value);
            List<Long> sends = new ArrayList<>();
            if (!json.isNull(SENDS_FIELD)) {
                JSONArray stored = json.getJSONArray(SENDS_FIELD);
                for (int i = 0; i < stored.length(); i++) {
                    sends.add(stored.getLong(i));
                }
            }
            Collections.sort(sends);
            return new RecoveryPhoneSendHistory(sends);
        } catch (JSONException e) {
            LOG.error("Unable to parse the recovery phone send history", e);
            return null;
        }
    }

}
