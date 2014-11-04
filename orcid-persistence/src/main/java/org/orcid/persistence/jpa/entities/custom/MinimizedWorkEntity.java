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
package org.orcid.persistence.jpa.entities.custom;

import java.io.Serializable;
import java.util.Date;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

/**
 * An object that will contain the minimum work information needed to display
 * work in the UI.
 * 
 * @author Angel Montenegro (amontenegro)
 * */
public class MinimizedWorkEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -6961089690820823167L;

    private Long id;
    private String title;
    private String subtitle;
    private String journalTitle;
    private String description;
    private int publicationDay;
    private int publicationMonth;
    private int publicationYear;
    private WorkType workType;
    private Visibility visibility;
    private String externalIdentifiersJson;
    private String languageCode;
    private String translatedTitleLanguageCode;
    private String translatedTitle;    
    private Long displayIndex;
    private String workUrl;
    private SourceEntity source;
    
    public MinimizedWorkEntity() {
        super();
    }

    public MinimizedWorkEntity(Long id, String title, String subtitle, String journalTitle, String description, Integer publicationDay, Integer publicationMonth, Integer publicationYear,
            Visibility visibility, String externalIdentifiersJson, Long displayIndex, SourceEntity source, Date dateCreated, Date lastModified,
            WorkType workType, String languageCode, String translatedTitleLanguageCode, String translatedTitle, String workUrl    
 ) {
        super();
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.journalTitle = journalTitle;
        this.description = description;
        if (publicationDay != null)
            this.publicationDay = publicationDay;
        if (publicationMonth != null)
            this.publicationMonth = publicationMonth;
        if (publicationYear != null)
            this.publicationYear = publicationYear;
        this.visibility = visibility;
        this.externalIdentifiersJson = externalIdentifiersJson;
        this.displayIndex = displayIndex;
        this.source = source;
        this.workType = workType;
        this.setDateCreated(dateCreated);
        this.setLastModified(lastModified);
        this.setLanguageCode(languageCode);
        this.setTranslatedTitleLanguageCode(translatedTitleLanguageCode);
        this.setTranslatedTitle(translatedTitle);
        this.setWorkUrl(workUrl);
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPublicationDay() {
        return publicationDay;
    }

    public void setPublicationDay(int publicationDay) {
        this.publicationDay = publicationDay;
    }

    public int getPublicationMonth() {
        return publicationMonth;
    }

    public void setPublicationMonth(int publicationMonth) {
        this.publicationMonth = publicationMonth;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }

    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getWorkUrl() {
        return workUrl;
    }

    public void setWorkUrl(String workUrl) {
        this.workUrl = workUrl;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }
}