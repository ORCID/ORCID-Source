package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

/**
 * An object that will contain the minimum work information needed to display
 * work in the UI.
 * 
 * @author Angel Montenegro (amontenegro)
 */
@MappedSuperclass
public class WorkBaseEntity extends SourceAwareEntity<Long> {

    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String title;
    protected String translatedTitle;
    protected String subtitle;
    protected String description;
    protected String workUrl;
    protected String journalTitle;
    protected String languageCode;
    protected String translatedTitleLanguageCode;
    protected String workType;
    protected PublicationDateEntity publicationDate;
    protected String externalIdentifiersJson;  
    protected String visibility;
    protected Long displayIndex;        

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

    @Transient
    public Integer getPublicationYear() {
        return publicationDate != null ? publicationDate.getYear() : null;
    }

    @Transient
    public Integer getPublicationMonth() {
        return publicationDate != null ? publicationDate.getMonth() : null;
    }

    @Transient
    public Integer getPublicationDay() {
        return publicationDate != null ? publicationDate.getDay() : null;
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

    @Column(name = "work_type", length = 100)
    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    @Column(name = "external_ids_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * Dictates the display order for works (and versions of works) works with
     * higher numbers should be displayed first.
     * 
     * Currently only updatable via ProfileWorkDaoImpl.updateToMaxDisplay
     *
     */
    @Column(name = "display_index", updatable = false)
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
}