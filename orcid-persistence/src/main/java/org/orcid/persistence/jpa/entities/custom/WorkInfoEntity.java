package org.orcid.persistence.jpa.entities.custom;

import java.util.SortedSet;

import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity;

public class WorkInfoEntity {
	private Long id;
	private int publicationDay;
	private int publicationMonth;
	private int publicationYear;
	private String title;
	private String translatedTitle;
	private String subtitle;
	private String description;
	private String workUrl;
	private String citation;
	private String journalTitle;
	private String languageCode;
	private String translatedTitleLanguageCode;
	private String sourceName;
	private Iso3166Country iso2Country;
	private CitationType citationType;
	private WorkType workType;
	private String contributorsJson;
	private SortedSet<WorkExternalIdentifierEntity> externalIdentifiers;
	
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getWorkUrl() {
		return workUrl;
	}
	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
	}
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
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
	public Iso3166Country getIso2Country() {
		return iso2Country;
	}
	public void setIso2Country(Iso3166Country iso2Country) {
		this.iso2Country = iso2Country;
	}
	public CitationType getCitationType() {
		return citationType;
	}
	public void setCitationType(CitationType citationType) {
		this.citationType = citationType;
	}
	public WorkType getWorkType() {
		return workType;
	}
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	public String getContributorsJson() {
		return contributorsJson;
	}
	public void setContributorsJson(String contributorsJson) {
		this.contributorsJson = contributorsJson;
	}
	public SortedSet<WorkExternalIdentifierEntity> getExternalIdentifiers() {
		return externalIdentifiers;
	}
	public void setExternalIdentifiers(
			SortedSet<WorkExternalIdentifierEntity> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}		
}
