/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities.keys;

import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * 2011-2012 - ORCID.
 *
 * @author Declan Newman (declan)
 *         Date: 07/08/2012
 */
@Embeddable
public class WorkExternalIdentifierEntityPk implements Serializable {

    private String identifier;
    private WorkExternalIdentifierType identifierType;
    private Long work;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public WorkExternalIdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(WorkExternalIdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public Long getWork() {
        return work;
    }

    public void setWork(Long work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkExternalIdentifierEntityPk)) {
            return false;
        }

        WorkExternalIdentifierEntityPk that = (WorkExternalIdentifierEntityPk) o;

        if (!identifier.equals(that.identifier)) {
            return false;
        }
        if (!identifierType.equals(that.identifierType)) {
            return false;
        }
        if (!work.equals(that.work)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + identifierType.hashCode();
        result = 31 * result + work.hashCode();
        return result;
    }
}
