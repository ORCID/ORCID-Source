package org.orcid.core.manager;

public interface RecoveryPhoneManager {

    /**
     * The stored recovery phone for the record, or null if there isn't one.
     * It carries the last four digits and the dates only, so it costs no
     * decryption: the Account settings panel polls this on every refresh.
     */
    RecoveryPhone getRecoveryPhone(String orcid);

    /**
     * The stored number in E.164 form, or null when there is none.
     *
     * This is the only path that decrypts the stored number, and it exists for
     * the flows that have to send a text to it without the user re-typing it.
     * The value must never be written to a log line, put in a RUM attribute key
     * or value, or returned over HTTP (R1.2): anything that reaches the user
     * carries the last four digits only.
     */
    String getDecryptedPhoneNumber(String orcid);

    /**
     * Stores the given E.164 number as the record's recovery phone, replacing
     * any existing one. The number is persisted reversibly encrypted, together
     * with the last four digits.
     *
     * @return the stored recovery phone as it now stands, from the row this
     *         call wrote. Callers answer from it rather than calling
     *         {@link #getRecoveryPhone(String)} straight after: that read goes
     *         to the read-only pool, which is a replica in a deployed
     *         environment and may not yet carry what was just written.
     */
    RecoveryPhone saveRecoveryPhone(String orcid, String e164PhoneNumber);

    /**
     * Removes the recovery phone, if any. Called when 2FA is disabled, since
     * turning 2FA off resets every 2FA backup option.
     */
    void removeRecoveryPhone(String orcid);

}
