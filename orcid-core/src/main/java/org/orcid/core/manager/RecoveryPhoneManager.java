package org.orcid.core.manager;

public interface RecoveryPhoneManager {

    /**
     * The stored recovery phone for the record, or null if there isn't one.
     */
    RecoveryPhone getRecoveryPhone(String orcid);

    /**
     * Stores the given E.164 number as the record's recovery phone, replacing
     * any existing one. Only a hash and the last four digits are persisted.
     *
     * @return true if this created a new recovery phone, false if it replaced
     *         an existing one.
     */
    boolean saveRecoveryPhone(String orcid, String e164PhoneNumber);

    /**
     * Removes the recovery phone, if any. Called when 2FA is disabled, since
     * turning 2FA off resets every 2FA backup option.
     */
    void removeRecoveryPhone(String orcid);

    /**
     * Whether the given E.164 number is the record's stored recovery phone.
     * The caller has to supply the number: we cannot recover it from storage.
     */
    boolean matches(String orcid, String e164PhoneNumber);

}
