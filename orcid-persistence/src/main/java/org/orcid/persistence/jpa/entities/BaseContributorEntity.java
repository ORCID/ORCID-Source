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
package org.orcid.persistence.jpa.entities;

import static org.orcid.utils.NullUtils.compareObjectsNullSafe;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.SequenceType;

/**
 * 2011-2012 - ORCID
 * 
 * @author Declan Newman (declan) Date: 08/08/2012
 */
@MappedSuperclass
public abstract class BaseContributorEntity extends BaseEntity<Long> implements Comparable<BaseContributorEntity>, ProfileAware {

    private static final long serialVersionUID = -371826957062237679L;
    private ProfileEntity profile;
    private String creditName;
    private String contributorEmail;
    private SequenceType sequence;
    private ContributorRole contributorRole;

    @Column(name = "credit_name", length = 450)
    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    @Column(name = "contributor_email", length = 300)
    public String getContributorEmail() {
        return contributorEmail;
    }

    public void setContributorEmail(String contributorEmail) {
        this.contributorEmail = contributorEmail;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "contributor_role", length = 90)
    public ContributorRole getContributorRole() {
        return contributorRole;
    }

    public void setContributorRole(ContributorRole contributorRole) {
        this.contributorRole = contributorRole;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "contributor_sequence", length = 90)
    public SequenceType getSequence() {
        return sequence;
    }

    public void setSequence(SequenceType sequence) {
        this.sequence = sequence;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.DETACH }, optional = true)
    @JoinColumn(name = "orcid", nullable = true, updatable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Override
    public int compareTo(BaseContributorEntity other) {
        if (other == null) {
            return -1;
        }
        int compareSequenceTypes = compareObjectsNullSafe(sequence, other.getSequence());
        if (compareSequenceTypes != 0) {
            return compareSequenceTypes;
        }
        int compareRoles = compareObjectsNullSafe(contributorRole, other.getContributorRole());
        if (compareRoles != 0) {
            return compareRoles;
        }
        int compareCreditNames = compareObjectsNullSafe(creditName, other.getCreditName());
        if (compareCreditNames != 0) {
            return compareCreditNames;
        }
        int compareEmails = compareObjectsNullSafe(contributorEmail, other.getContributorEmail());
        if (compareEmails != 0) {
            return compareEmails;
        }
        return compareCreditNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contributorEmail == null) ? 0 : contributorEmail.hashCode());
        result = prime * result + ((contributorRole == null) ? 0 : contributorRole.hashCode());
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((profile == null) ? 0 : profile.hashCode());
        result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseContributorEntity other = (BaseContributorEntity) obj;
        if (contributorEmail == null) {
            if (other.contributorEmail != null)
                return false;
        } else if (!contributorEmail.equals(other.contributorEmail))
            return false;
        if (contributorRole != other.contributorRole)
            return false;
        if (creditName == null) {
            if (other.creditName != null)
                return false;
        } else if (!creditName.equals(other.creditName))
            return false;
        if (profile == null) {
            if (other.profile != null)
                return false;
        } else if (!profile.equals(other.profile))
            return false;
        if (sequence != other.sequence)
            return false;
        return true;
    }

}
