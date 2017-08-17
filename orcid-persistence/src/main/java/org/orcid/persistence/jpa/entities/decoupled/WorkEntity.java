package org.orcid.persistence.jpa.entities.decoupled;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.persistence.jpa.entities.DisplayIndexInterface;
import org.orcid.persistence.jpa.entities.ProfileAware;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;

public class WorkEntity extends WorkBaseEntity implements Comparable<WorkEntity>, DisplayIndexInterface {
    private String citation;
    private Iso3166Country iso2Country;
    private CitationType citationType;
    private String contributorsJson;
    private Date addedToProfileDate;

    @Column(name = "citation", length = 5000)
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "citation_type", length = 100)
    public CitationType getCitationType() {
        return citationType;
    }

    public void setCitationType(CitationType citationType) {
        this.citationType = citationType;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "iso2_country", length = 2)
    public Iso3166Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso3166Country iso2Country) {
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

        int comparison = compareOrcidId(other);
        if (comparison == 0) {
            comparison = comparePublicationDate(other);
            if (comparison == 0) {
                comparison = compareTitles(other);
                if (comparison == 0) {
                    return compareIds(other);
                }
            }
        }

        return comparison;
    }

    private int compareTitles(WorkEntity other) {
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

    private int compareIds(WorkEntity other) {
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

    private int comparePublicationDate(WorkEntity other) {
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

    private int compareOrcidId(WorkEntity other) {
        if (this.getProfile() == null) {
            if (other.getProfile() == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (other.getProfile() == null) {
            return 1;
        } else {
            return this.getProfile().getId().compareTo(other.getProfile().getId());
        }
    }
}
