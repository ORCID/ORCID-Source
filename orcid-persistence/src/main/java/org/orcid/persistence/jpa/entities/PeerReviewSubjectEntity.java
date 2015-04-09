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

import static org.orcid.utils.NullUtils.compareObjectsNullSafe;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.WorkType;
import org.orcid.utils.OrcidStringUtils;

@Entity
@Table(name = "peer_review_subject")
public class PeerReviewSubjectEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 4488839570068368532L;
    private Long id;
    private String externalIdentifiersJson;
    private WorkType workType;
    private String journalTitle;
    private String title;
    private String subTitle;
    private String translatedTitle;
    private String translatedTitleLanguageCode;
    private String url;    
        
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "peer_review_subject_seq")
    @SequenceGenerator(name = "peer_review_subject_seq", sequenceName = "peer_review_subject_seq")   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "external_identifiers_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", length = 100)
    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    @Column(name = "journal_title")
    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "translated_title")
    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @Column(name = "translated_title_language_code")
    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }

    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "sub_title")
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }            

    public int compareTo(PeerReviewSubjectEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        int titlesCompare = OrcidStringUtils.compareStrings(title, other.getTitle());
        if (titlesCompare != 0) {
            return titlesCompare;
        }

        int subtitleCompare = OrcidStringUtils.compareStrings(subTitle, other.getSubTitle());
        if(subtitleCompare != 0) {
            return subtitleCompare;
        }
        
        int typeCompare = compareObjectsNullSafe(workType, other.getWorkType());
        if(typeCompare != 0) {
            return typeCompare;
        }
        
        int urlCompare =  OrcidStringUtils.compareStrings(url, other.getUrl());
        if(urlCompare != 0) {
            return urlCompare;
        }
        
        int journalTitleCompare = OrcidStringUtils.compareStrings(journalTitle, other.getJournalTitle());
        if (journalTitleCompare != 0) {
            return journalTitleCompare;
        }
        
        int compareExternalIds = OrcidStringUtils.compareStrings(externalIdentifiersJson, other.getExternalIdentifiersJson());
        if(compareExternalIds != 0) {
            return compareExternalIds;
        }

        return 0;
    }    
}
