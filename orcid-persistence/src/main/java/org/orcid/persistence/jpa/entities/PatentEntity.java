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
import org.orcid.jaxb.model.message.Iso3166Country;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.SortedSet;

/**
 * orcid-entities - Dec 6, 2011 - WorkEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "patent")
public class PatentEntity extends BaseEntity<Long> implements Comparable<PatentEntity> {

    private static final long serialVersionUID = -8096348611438944935L;
    private static final String PATENT = "patent";

    private Long id;
    private Iso3166Country countryOfIssue;
    private String patentNo;
    private String shortDescription;
    private Date issueDate;
    private SortedSet<PatentContributorEntity> contributors;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "patent_seq")
    @SequenceGenerator(name = "patent_seq", sequenceName = "patent_seq")
    @Column(name = "patent_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "issuing_country", length = 155)
    public Iso3166Country getCountryOfIssue() {
        return countryOfIssue;
    }

    public void setCountryOfIssue(Iso3166Country countryOfIssue) {
        this.countryOfIssue = countryOfIssue;
    }

    @Column(name = "patent_no", length = 60)
    public String getPatentNo() {
        return patentNo;
    }

    public void setPatentNo(String patentNo) {
        this.patentNo = patentNo;
    }

    @Column(name = "short_description", length = 550)
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Column(name = "issue_date")
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * @return the contributors
     */
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = PATENT)
    @Fetch(FetchMode.SUBSELECT)
    @Sort(type = SortType.NATURAL)
    public SortedSet<PatentContributorEntity> getContributors() {
        return contributors;
    }

    /**
     * @param contributors
     *            the authors to set
     */
    public void setContributors(SortedSet<PatentContributorEntity> contributors) {
        this.contributors = contributors;
    }

    private int compareIds(PatentEntity other) {
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

    @Override
    public int compareTo(PatentEntity patentEntity) {
        return compareIds(patentEntity);
    }
}
