package org.orcid.internal.util;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * The answer to an account recovery match check.
 *
 * Only ever states whether the submitted iD and email belong together. A caller cannot use it to
 * discover whether an email address is registered, or to whom: every kind of non-match - unknown
 * email, unknown iD, or an email that belongs to a different record - produces the same response.
 * The record status is disclosed only once the pair is confirmed to match.
 */
public class AccountRecoveryMatchResponse {

    @XmlElement(name = "match")
    private boolean match;

    @XmlElement(name = "recordStatus")
    private RecordStatus recordStatus;

    public enum RecordStatus {
        ACTIVE, LOCKED, DEACTIVATED, UNCLAIMED, DEPRECATED;
    }

    public static AccountRecoveryMatchResponse noMatch() {
        return new AccountRecoveryMatchResponse(false, null);
    }

    public static AccountRecoveryMatchResponse match(RecordStatus recordStatus) {
        return new AccountRecoveryMatchResponse(true, recordStatus);
    }

    public AccountRecoveryMatchResponse() {
    }

    public AccountRecoveryMatchResponse(boolean match, RecordStatus recordStatus) {
        this.match = match;
        this.recordStatus = recordStatus;
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
}
