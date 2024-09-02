package org.orcid.persistence.jpa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.orcid.utils.NullUtils;

/**
 * @author Declan Newman (declan) Date: 08/08/2012
 */
@MappedSuperclass
public abstract class BaseContributorEntity extends BaseEntity<Long> implements Comparable<BaseContributorEntity>, OrcidAware {

    private static final long serialVersionUID = -371826957062237679L;
    private String orcid;
    private String creditName;
    private String contributorEmail;
    private String sequence;
    private String contributorRole;

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

    @Column(name = "contributor_role", length = 90)
    public String getContributorRole() {
        return contributorRole;
    }

    public void setContributorRole(String contributorRole) {
        this.contributorRole = contributorRole;
    }

    @Column(name = "contributor_sequence", length = 90)
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Column(name = "orcid", nullable = true, updatable = false)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Override
    public int compareTo(BaseContributorEntity other) {
        if (other == null) {
            return -1;
        }
        int compareSequenceTypes = NullUtils.compareObjectsNullSafe(sequence, other.getSequence());
        if (compareSequenceTypes != 0) {
            return compareSequenceTypes;
        }
        int compareRoles = NullUtils.compareObjectsNullSafe(contributorRole, other.getContributorRole());
        if (compareRoles != 0) {
            return compareRoles;
        }
        int compareCreditNames = NullUtils.compareObjectsNullSafe(creditName, other.getCreditName());
        if (compareCreditNames != 0) {
            return compareCreditNames;
        }
        int compareEmails = NullUtils.compareObjectsNullSafe(contributorEmail, other.getContributorEmail());
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
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
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
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (sequence != other.sequence)
            return false;
        return true;
    }

}
