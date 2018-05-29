package org.orcid.persistence.jpa.entities;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "work")
public class WorkEntity extends WorkBaseEntity implements Comparable<WorkEntity>, DisplayIndexInterface, OrcidAware {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1938355431548785244L;
    protected String orcid;
    protected String citation;
    protected String iso2Country;
    protected String citationType;
    protected String contributorsJson;
    protected Date addedToProfileDate;
    
    @Column(name = "orcid", updatable = false, insertable = true)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "citation", length = 5000)
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Column(name = "citation_type", length = 100)
    public String getCitationType() {
        return citationType;
    }

    public void setCitationType(String citationType) {
        this.citationType = citationType;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    @Column(name = "iso2_country", length = 2)
    public String getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(String iso2Country) {
        this.iso2Country = iso2Country;
    }

    @Column(name = "added_to_profile_date")
    public Date getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(Date addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
    }
    
    @Override
    public int compareTo(WorkEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        int comparison = comparePublicationDate(other);
        if (comparison == 0) {
            comparison = compareTitles(other);
            if (comparison == 0) {
                return compareIds(other);
            }            
        }

        return comparison;
    }

    protected int compareTitles(WorkEntity other) {
        if (other.getTitle() == null) {
            if (title == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (title == null) {
            return -1;
        }
        return title.compareToIgnoreCase(other.getTitle());
    }

    protected int compareIds(WorkEntity other) {
        if (other.getId() == null) {
            if (id == null) {
                if (equals(other)) {
                    return 0;
                } else {
                    // If can't determine preferred order, then be polite and
                    // say 'after you!'
                    return -1;
                }
            } else {
                return 1;
            }
        }
        if (id == null) {
            return -1;
        }
        return id.compareTo(other.getId());
    }

    protected int comparePublicationDate(WorkEntity other) {
        if (other.getPublicationDate() == null) {
            if (this.publicationDate == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (this.publicationDate == null) {
            return -1;
        }

        return this.publicationDate.compareTo(other.getPublicationDate());
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
