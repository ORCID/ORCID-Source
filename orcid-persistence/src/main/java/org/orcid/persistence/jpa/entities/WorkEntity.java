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
package org.orcid.persistence.jpa.entities;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.common_v2.Iso3166Country;

/**
 * orcid-entities - Dec 6, 2011 - WorkEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "work")
public class WorkEntity extends org.orcid.persistence.jpa.entities.decoupled.WorkEntity implements Comparable<WorkEntity>, ProfileAware, DisplayIndexInterface {

    private static final long serialVersionUID = 1L;

    private ProfileEntity profile;

    /**
     * @return the profile
     */
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "orcid", nullable = true)
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile
     *            the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    

    

    public static class ChronologicallyOrderedWorkEntityComparator implements Comparator<WorkEntity> {
        public int compare(WorkEntity work1, WorkEntity work2) {
            if (work2 == null) {
                throw new NullPointerException("Can't compare with null");
            }

            // Negate the result (Multiply it by -1) to reverse the order.
            int comparison = work1.comparePublicationDate(work2) * -1;

            if (comparison == 0) {
                comparison = work1.compareTitles(work2);
                if (comparison == 0) {
                    return work1.compareIds(work2);
                }
            }

            return comparison;
        }
    }

    /**
     * Clean simple fields so that entity can be reused.
     */
    public void clean() {
        title = null;
        subtitle = null;
        description = null;
        workUrl = null;
        citation = null;
        citationType = null;
        workType = null;
        publicationDate = null;
        journalTitle = null;
        languageCode = null;
        iso2Country = null;
    }

}
