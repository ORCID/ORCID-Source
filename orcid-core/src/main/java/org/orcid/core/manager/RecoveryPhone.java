package org.orcid.core.manager;

import java.util.Date;

/**
 * The stored recovery phone for a record, as far as anything outside the
 * persistence layer is allowed to see it: the last four digits and the dates.
 * The number itself never leaves the database in a readable form.
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
