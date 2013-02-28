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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.jaxb.model.message.Visibility;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

/**
 * orcid-entities - Dec 6, 2011 - WorkEntity
 * 
 * @author Declan Newman (declan)
 */
// Table name is funding_grant to avoid the obvious SQL naming problems
@Entity
@Table(name = "funding_grant")
public class GrantEntity extends BaseEntity<Long> implements Comparable<GrantEntity> {

    private static final long serialVersionUID = -8096348611438944935L;
    private static final String GRANT = "grant";

    private Long id;
    private String agencyName;
    private ProfileEntity agencyOrcid;
    private String grantNo;
    private String grantExternalId;
    private String grantExternalProgram;
    private String shortDescription;
    private Date grantDate;
    private SortedSet<GrantContributorEntity> contributors;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "grant_seq")
    @SequenceGenerator(name = "grant_seq", sequenceName = "grant_seq")
    @Column(name = "grant_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "agency_name", length = 255)
    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
    @JoinColumn(name = "agency_orcid", nullable = true, updatable = false, insertable = false)
    public ProfileEntity getAgencyOrcid() {
        return agencyOrcid;
    }

    public void setAgencyOrcid(ProfileEntity agencyOrcid) {
        this.agencyOrcid = agencyOrcid;
    }

    @Column(name = "grant_no", length = 40)
    public String getGrantNo() {
        return grantNo;
    }

    @Column(name = "grant_external_id", length = 155)
    public String getGrantExternalId() {
        return grantExternalId;
    }

    public void setGrantExternalId(String grantExternalId) {
        this.grantExternalId = grantExternalId;
    }

    @Column(name = "grant_external_program", length = 155)
    public String getGrantExternalProgram() {
        return grantExternalProgram;
    }

    public void setGrantExternalProgram(String grantExternalProgram) {
        this.grantExternalProgram = grantExternalProgram;
    }

    public void setGrantNo(String grantNo) {
        this.grantNo = grantNo;
    }

    @Column(name = "short_description", length = 550)
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Column(name = "grant_date")
    public Date getGrantDate() {
        return grantDate;
    }

    public void setGrantDate(Date grantDate) {
        this.grantDate = grantDate;
    }

    /**
     * @return the authors
     */
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = GRANT)
    @Fetch(FetchMode.SUBSELECT)
    @Sort(type = SortType.NATURAL)
    public SortedSet<GrantContributorEntity> getContributors() {
        return contributors;
    }

    /**
     * @param contributors
     *            the authors to set
     */
    public void setContributors(SortedSet<GrantContributorEntity> contributors) {
        this.contributors = contributors;
    }

    @Override
    public int compareTo(GrantEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }
        int titleComparison = compareGrants(other);
        if (titleComparison == 0) {
            return compareIds(other);
        }

        return titleComparison;
    }

    private int compareGrants(GrantEntity other) {
        if (other.getAgencyName() == null) {
            if (agencyName == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (agencyName == null) {
            return -1;
        }
        return agencyName.compareTo(other.getAgencyName());
    }

    private int compareIds(GrantEntity other) {
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
}
