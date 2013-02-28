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
public class AuthorWorkEntityPk implements Serializable {

    private static final long serialVersionUID = -6483017414290915958L;

    private Long author;
    private Long work;

    /**
     * @return the profile
     */
    public Long getAuthor() {
        return author;
    }

    /**
     * @param author
     *            the profile to set
     */
    public void setAuthor(Long author) {
        this.author = author;
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

        AuthorWorkEntityPk that = (AuthorWorkEntityPk) o;

        if (!author.equals(that.author))
            return false;
        if (!work.equals(that.work))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = author.hashCode();
        result = 31 * result + work.hashCode();
        return result;
    }
}
