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

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntityPk
 * 
 * @author Declan Newman (declan)
 */
@Embeddable
public class SecondaryAuthorWorkEntityPk implements Serializable {

    private static final long serialVersionUID = -6483017414290915958L;

    private Long secondaryAuthor;
    private Long work;

    /**
     * @return the profile
     */
    public Long getSecondaryAuthor() {
        return secondaryAuthor;
    }

    /**
     * @param secondaryAuthor
     *            the profile to set
     */
    public void setSecondaryAuthor(Long secondaryAuthor) {
        this.secondaryAuthor = secondaryAuthor;
    }

    /**
     * @return the work
     */
    public Long getWork() {
        return work;
    }

    /**
     * @param work
     *            the work to set
     */
    public void setWork(Long work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SecondaryAuthorWorkEntityPk that = (SecondaryAuthorWorkEntityPk) o;

        if (!secondaryAuthor.equals(that.secondaryAuthor))
            return false;
        if (!work.equals(that.work))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = secondaryAuthor.hashCode();
        result = 31 * result + work.hashCode();
        return result;
    }
}
