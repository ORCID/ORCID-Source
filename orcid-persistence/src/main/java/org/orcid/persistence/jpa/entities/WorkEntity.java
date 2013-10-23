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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.NewWorkType;
import org.orcid.jaxb.model.message.WorkSubtype;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * orcid-entities - Dec 6, 2011 - WorkEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "work")
public class WorkEntity extends BaseEntity<Long> implements Comparable<WorkEntity> {

    private static final long serialVersionUID = -8096348611438944935L;
    private static final String WORK = "work";

    private Long id;
    private String title;
    private String translatedTitle;
    private String subtitle;
    private String description;
    private String workUrl;
    private String citation;
    private String journalTitle;
    private String languageCode;
    private String translatedTitleLanguageCode;
    private Iso3166Country iso2Country;
    private CitationType citationType;
    private NewWorkType workType;
    private WorkSubtype workSubtype;
    private PublicationDateEntity publicationDate;
    private String contributorsJson;
    private SortedSet<WorkContributorEntity> contributors;
    private SortedSet<WorkExternalIdentifierEntity> externalIdentifiers;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "work_seq")
    @SequenceGenerator(name = "work_seq", sequenceName = "work_seq")
    @Column(name = "work_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the publicationDate
     */
    @Column(name = "publication_date")
    public PublicationDateEntity getPublicationDate() {
        return publicationDate;
    }

    /**
     * @param publicationDate
     *            the publicationDate to set
     */
    public void setPublicationDate(PublicationDateEntity publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * @return the titles
     */
    @Column(name = "title", length = 1000)
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "subtitle", length = 1000)
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Column(name = "description", length = 5000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "work_url", length = 350)
    public String getWorkUrl() {
        return workUrl;
    }

    public void setWorkUrl(String workUrl) {
        this.workUrl = workUrl;
    }

    @Column(name = "citation", length = 5000)
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Column(name = "journal_title", length = 1000)
    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    
    @Column(name = "language_code", length = 25)
    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Column(name = "translated_title", length = 25)
    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @Column(name = "translated_title_language_code", length = 25)
    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }

    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
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

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", length = 100)
    public NewWorkType getWorkType() {
        return workType;
    }

    public void setWorkType(NewWorkType workType) {
        this.workType = workType;
    }
            
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "work_subtype", length = 100)
    public WorkSubtype getWorkSubtype() {
        return workSubtype;
    }

    public void setWorkSubtype(WorkSubtype workSubtype) {
        this.workSubtype = workSubtype;
    }
    
    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    /**
     * @return the authors
     */
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = WORK, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Sort(type = SortType.NATURAL)
    public SortedSet<WorkContributorEntity> getContributors() {
        return contributors;
    }

    /**
     * @param contributors
     *            the authors to set
     */
    public void setContributors(SortedSet<WorkContributorEntity> contributors) {
        this.contributors = contributors;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = WORK, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Sort(type = SortType.NATURAL)
    public SortedSet<WorkExternalIdentifierEntity> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(SortedSet<WorkExternalIdentifierEntity> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
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
