package org.orcid.core.manager;

import java.util.Date;

/**
 * The stored recovery phone for a record, as far as anything outside the
 * persistence layer is allowed to see it: the last four digits and the dates.
 * The number itself never leaves the database in a readable form through this
 * object. The one path that reads it is
 * {@link RecoveryPhoneManager#getDecryptedPhoneNumber(String)}, kept separate
 * so that the callers who only want the mask and the dates never decrypt.
 */
public class RecoveryPhone {

    private final String lastFour;

    private final Date dateCreated;

    private final Date lastModified;

    public RecoveryPhone(String lastFour, Date dateCreated, Date lastModified) {
        this.lastFour = lastFour;
        this.dateCreated = dateCreated;
        this.lastModified = lastModified;
    }

    public String getLastFour() {
        return lastFour;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

}
