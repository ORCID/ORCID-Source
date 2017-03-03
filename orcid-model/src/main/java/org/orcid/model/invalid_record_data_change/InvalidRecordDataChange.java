/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.model.invalid_record_data_change;

import java.util.Date;

public class InvalidRecordDataChange {
    private Long sequence;
    private String sqlUsedToUpdate;
    private String description;
    private Long numChanged;
    private String type;
    private Date dateCreated;
    private Date lastModified;

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getSqlUsedToUpdate() {
        return sqlUsedToUpdate;
    }

    public void setSqlUsedToUpdate(String sqlUsedToUpdate) {
        this.sqlUsedToUpdate = sqlUsedToUpdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNumChanged() {
        return numChanged;
    }

    public void setNumChanged(Long numChanged) {
        this.numChanged = numChanged;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
