package org.orcid.persistence.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.WorkType;

@Entity
@Table(name = "peer_review_subject")
public class PeerReviewSubjectEntity {

    private String externalIdentifiersJson;
    private WorkType workType;
    private String journalTitle;
    private String title;
    private String translatedTitle;
    private String translatedTitleLanguageCode;
    private String workUrl;
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }
    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }
    public WorkType getWorkType() {
        return workType;
    }
    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }
    public String getJournalTitle() {
        return journalTitle;
    }
    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTranslatedTitle() {
        return translatedTitle;
    }
    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }
    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }
    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
    }
    public String getWorkUrl() {
        return workUrl;
    }
    public void setWorkUrl(String workUrl) {
        this.workUrl = workUrl;
    }        
}
