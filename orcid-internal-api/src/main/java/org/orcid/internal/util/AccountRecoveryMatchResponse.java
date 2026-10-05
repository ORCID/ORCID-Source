package org.orcid.internal.util;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The answer to an account recovery match check.
 *
 * Only ever states whether the submitted iD and email belong together. A caller cannot use it to
 * discover whether an email address is registered, or to whom: every kind of non-match - unknown
 * email, unknown iD, or an email that belongs to a different record - produces the same response.
 * The record status, and the record's email addresses, are disclosed only once the pair is
 * confirmed to match.
 */
public class AccountRecoveryMatchResponse {

    @XmlElement(name = "match")
    private boolean match;

    @XmlElement(name = "recordStatus")
    private RecordStatus recordStatus;

    /**
     * Every email address on the record, verified or not and whatever its visibility, so the
     * recovery workflow can test whether any of them is still read before releasing the account.
     * Set only on a match. A non match leaves it null, and null is left out of the JSON altogether,
     * so a non match reads exactly as it did before the field existed.
     */
    @XmlElement(name = "emails")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> emails;

    public enum RecordStatus {
        ACTIVE, LOCKED, DEACTIVATED, UNCLAIMED, DEPRECATED;
    }

    public static AccountRecoveryMatchResponse noMatch() {
        return new AccountRecoveryMatchResponse(false, null, null);
    }

    public static AccountRecoveryMatchResponse match(RecordStatus recordStatus, List<String> emails) {
        return new AccountRecoveryMatchResponse(true, recordStatus, emails);
    }

    public AccountRecoveryMatchResponse() {
    }

    public AccountRecoveryMatchResponse(boolean match, RecordStatus recordStatus, List<String> emails) {
        this.match = match;
        this.recordStatus = recordStatus;
        this.emails = emails;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
