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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;

public class Work implements ErrorsInterface, Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> errors = new ArrayList<String>();

	private Date publicationDate;

	private Visibility visibility;

	private Text putCode;

	private Text shortDescription;

	private Text url;

	private Text journalTitle;

	private Text languageCode;

	private Text languageName;

	private Citation citation;

	private Text countryCode;

	private Text countryName;

	private List<Contributor> contributors;

	private List<WorkExternalIdentifier> workExternalIdentifiers;

	private Text workSource;

	private Text workSourceName;

	private WorkTitle workTitle;

	private Text workCategory;

	private Text workType;

	protected String citationForDisplay;

	public static Work valueOf(MinimizedWorkEntity minimizedWorkEntity) {
		Work w = new Work();
		// Set id
		w.setPutCode(Text.valueOf(String.valueOf(minimizedWorkEntity.getId())));
		// Set publication date
		Integer year = (minimizedWorkEntity.getPublicationYear() <= 0) ? null
				: minimizedWorkEntity.getPublicationYear();
		Integer month = (minimizedWorkEntity.getPublicationMonth() <= 0) ? null
				: minimizedWorkEntity.getPublicationMonth();
		Integer day = (minimizedWorkEntity.getPublicationDay() <= 0) ? null
				: minimizedWorkEntity.getPublicationDay();
		w.setPublicationDate(Date.valueOf(new FuzzyDate(year, month, day)));
		// Set title and subtitle
		if (!StringUtils.isEmpty(minimizedWorkEntity.getTitle())) {
			WorkTitle workTitle = new WorkTitle();
			Text title = Text.valueOf(minimizedWorkEntity.getTitle());
			workTitle.setTitle(title);
			if (!StringUtils.isEmpty(minimizedWorkEntity.getSubtitle())) {
				Text subtitle = Text.valueOf(minimizedWorkEntity.getSubtitle());
				workTitle.setSubtitle(subtitle);
			}
			w.setWorkTitle(workTitle);
		}
		// Set description
		if (!StringUtils.isEmpty(minimizedWorkEntity.getDescription())) {
			w.setShortDescription(Text.valueOf(minimizedWorkEntity
					.getDescription()));
		}
		// Set visibility
		if (minimizedWorkEntity.getVisibility() != null)
			w.setVisibility(minimizedWorkEntity.getVisibility());

		return w;
	}

	public static Work valueOf(ProfileWorkEntity profileWorkEntity,
			WorkEntity workEntity, WorkContributors workContributors,
			WorkExternalIdentifiers workExternalIdentifiers) {
		if (workEntity == null || profileWorkEntity == null)
			return null;
		Work work = new Work();
		// Set id
		if (workEntity.getId() != null)
			work.setPutCode(Text.valueOf(String.valueOf(workEntity.getId())));
		// Set publication date
		if (workEntity.getPublicationDate() != null) {
			Integer year = (workEntity.getPublicationDate().getYear() == null || workEntity
					.getPublicationDate().getYear() <= 0) ? null : workEntity
					.getPublicationDate().getYear();
			Integer month = (workEntity.getPublicationDate().getMonth() == null || workEntity
					.getPublicationDate().getMonth() <= 0) ? null : workEntity
					.getPublicationDate().getMonth();
			Integer day = (workEntity.getPublicationDate().getDay() == null || workEntity
					.getPublicationDate().getDay() <= 0) ? null : workEntity
					.getPublicationDate().getDay();
			work.setPublicationDate(Date
					.valueOf(new FuzzyDate(year, month, day)));
		}
		// Set short description
		if (StringUtils.isNotEmpty(workEntity.getDescription()))
			work.setShortDescription(Text.valueOf(workEntity.getDescription()));
		// Set URL
		if (StringUtils.isNotEmpty(workEntity.getWorkUrl()))
			work.setUrl(Text.valueOf(workEntity.getWorkUrl()));
		// Set visibility
		if (profileWorkEntity.getVisibility() != null)
			work.setVisibility(profileWorkEntity.getVisibility());
		// Set citation
		if (StringUtils.isNotEmpty(workEntity.getCitation())) {
			Citation citation = new Citation();
			citation.setCitation(Text.valueOf(workEntity.getCitation()));
			if (workEntity.getCitationType() != null) {
				citation.setCitationType(Text.valueOf(workEntity
						.getCitationType().value()));
			}
			work.setCitation(citation);
		}
		// Set work contributors
		if (workContributors != null
				&& workContributors.getContributor() != null) {
			List<Contributor> contributors = new ArrayList<Contributor>();
			for (org.orcid.jaxb.model.message.Contributor owContributor : workContributors
					.getContributor()) {
				contributors.add(Contributor.valueOf(owContributor));
			}
			work.setContributors(contributors);
		}
		// Set external identifiers
		if (workExternalIdentifiers != null
				&& workExternalIdentifiers.getWorkExternalIdentifier() != null) {
			List<WorkExternalIdentifier> externalIdentifiers = new ArrayList<WorkExternalIdentifier>();
			for (org.orcid.jaxb.model.message.WorkExternalIdentifier owWorkExternalIdentifier : workExternalIdentifiers
					.getWorkExternalIdentifier()) {
				externalIdentifiers.add(WorkExternalIdentifier
						.valueOf(owWorkExternalIdentifier));
			}
			work.setWorkExternalIdentifiers(externalIdentifiers);
		}
		// Set source name
		if (profileWorkEntity.getSourceProfile() != null) {
			ProfileEntity sourceEntity = profileWorkEntity.getSourceProfile();
			String sourceName = new String();
			Visibility sourceNameVisibility = (sourceEntity
					.getCreditNameVisibility() == null) ? OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT
					.getVisibility() : sourceEntity.getCreditNameVisibility();
			if (OrcidType.CLIENT.equals(sourceEntity.getOrcidType())) {
				if (Visibility.PUBLIC.equals(sourceNameVisibility)) {
					sourceName = sourceEntity.getCreditName();
				}
			} else {
				// If it is a user, check if it have a credit name and is
				// visible
				if (Visibility.PUBLIC.equals(sourceNameVisibility)) {
					sourceName = sourceEntity.getCreditName();
				} else {
					// If it doesnt, lets use the give name + family name
					sourceName = sourceEntity.getGivenNames()
							+ (StringUtils
									.isEmpty(sourceEntity.getFamilyName()) ? ""
									: " " + sourceEntity.getFamilyName());
				}
			}
			work.setWorkSourceName(Text.valueOf(sourceName));
		}
		// Set title
		if (StringUtils.isNotEmpty(workEntity.getTitle())) {
			WorkTitle workTitle = new WorkTitle();
			workTitle.setTitle(Text.valueOf(workEntity.getTitle()));
			if (StringUtils.isNotEmpty(workEntity.getSubtitle())) {
				workTitle.setSubtitle(Text.valueOf(workEntity.getSubtitle()));
			}
			if (StringUtils.isNotEmpty(workEntity.getTranslatedTitle())) {
				TranslatedTitle translatedTitle = new TranslatedTitle();
				translatedTitle.setContent(workEntity.getTranslatedTitle());
				translatedTitle.setLanguageCode(workEntity
						.getTranslatedTitleLanguageCode());
				workTitle.setTranslatedTitle(translatedTitle);
			}
			work.setWorkTitle(workTitle);
		}
		// Set type
		if (workEntity.getWorkType() != null)
			work.setWorkType(Text.valueOf(workEntity.getWorkType().value()));
		// Set journal title
		if (StringUtils.isNotEmpty(workEntity.getJournalTitle()))
			work.setJournalTitle(Text.valueOf(workEntity.getJournalTitle()));
		// Set language code
		if (StringUtils.isNotEmpty(workEntity.getLanguageCode()))
			work.setLanguageCode(Text.valueOf(workEntity.getLanguageCode()));
		// Set country code
		if (workEntity.getIso2Country() != null)
			work.setCountryCode(Text.valueOf(workEntity.getIso2Country()
					.value()));

		return work;
	}

	public static Work valueOf(OrcidWork orcidWork) {
		Work w = new Work();
		if (orcidWork.getPublicationDate() != null)
			w.setPublicationDate(Date.valueOf(orcidWork.getPublicationDate()));
		if (orcidWork.getPutCode() != null)
			w.setPutCode(Text.valueOf(orcidWork.getPutCode()));
		if (orcidWork.getShortDescription() != null)
			w.setShortDescription(Text.valueOf(orcidWork.getShortDescription()));
		if (orcidWork.getUrl() != null)
			w.setUrl(Text.valueOf(orcidWork.getUrl().getValue()));
		if (orcidWork.getVisibility() != null)
			w.setVisibility(orcidWork.getVisibility());
		if (orcidWork.getWorkCitation() != null)
			w.setCitation(Citation.valueOf(orcidWork.getWorkCitation()));

		if (orcidWork.getWorkContributors() != null
				&& orcidWork.getWorkContributors().getContributor() != null) {
			List<Contributor> contributors = new ArrayList<Contributor>();
			for (org.orcid.jaxb.model.message.Contributor owContributor : orcidWork
					.getWorkContributors().getContributor()) {
				contributors.add(Contributor.valueOf(owContributor));
			}
			w.setContributors(contributors);
		}
		if (orcidWork.getWorkExternalIdentifiers() != null
				&& orcidWork.getWorkExternalIdentifiers()
						.getWorkExternalIdentifier() != null) {
			List<WorkExternalIdentifier> workExternalIdentifiers = new ArrayList<WorkExternalIdentifier>();
			for (org.orcid.jaxb.model.message.WorkExternalIdentifier owWorkExternalIdentifier : orcidWork
					.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {
				workExternalIdentifiers.add(WorkExternalIdentifier
						.valueOf(owWorkExternalIdentifier));
			}
			w.setWorkExternalIdentifiers(workExternalIdentifiers);
		}
		if (orcidWork.getWorkSource() != null) {
			w.setWorkSource(Text.valueOf(orcidWork.getWorkSource().getPath()));
			if (orcidWork.getWorkSource().getSourceName() != null)
				w.setWorkSourceName(Text.valueOf(orcidWork.getWorkSource()
						.getSourceName()));
		}
		if (orcidWork.getWorkTitle() != null)
			w.setWorkTitle(WorkTitle.valueOf(orcidWork.getWorkTitle()));
		if (orcidWork.getWorkType() != null)
			w.setWorkType(Text.valueOf(orcidWork.getWorkType().value()));

		if (orcidWork.getJournalTitle() != null)
			w.setJournalTitle(Text.valueOf(orcidWork.getJournalTitle()
					.getContent()));

		if (orcidWork.getLanguageCode() != null)
			w.setLanguageCode(Text.valueOf(orcidWork.getLanguageCode()));

		if (orcidWork.getCountry() != null)
			w.setCountryCode((orcidWork.getCountry().getValue() == null) ? null
					: Text.valueOf(orcidWork.getCountry().getValue().value()));
		return w;
	}

	public OrcidWork toOrcidWork() {
		OrcidWork ow = new OrcidWork();
		if (this.getPublicationDate() != null)
			ow.setPublicationDate(new PublicationDate(this.getPublicationDate()
					.toFuzzyDate()));
		if (this.getPutCode() != null)
			ow.setPutCode(this.getPutCode().getValue());
		if (this.getShortDescription() != null)
			ow.setShortDescription(this.shortDescription.getValue());
		if (this.getUrl() != null)
			ow.setUrl(new Url(this.url.getValue()));
		if (this.getVisibility() != null)
			ow.setVisibility(this.getVisibility());
		if (this.getCitation() != null)
			ow.setWorkCitation(this.citation.toCitiation());
		if (this.getContributors() != null) {
			List<org.orcid.jaxb.model.message.Contributor> cList = new ArrayList<org.orcid.jaxb.model.message.Contributor>();
			for (Contributor c : this.getContributors()) {
				cList.add(c.toContributor());
			}
			ow.setWorkContributors(new WorkContributors(cList));
		}
		if (this.getWorkExternalIdentifiers() != null) {
			List<org.orcid.jaxb.model.message.WorkExternalIdentifier> wiList = new ArrayList<org.orcid.jaxb.model.message.WorkExternalIdentifier>();
			for (WorkExternalIdentifier wi : this.getWorkExternalIdentifiers()) {
				wiList.add(wi.toWorkExternalIdentifier());
			}
			ow.setWorkExternalIdentifiers(new WorkExternalIdentifiers(wiList));
		}
		if (this.getWorkSource() != null)
			ow.setWorkSource(new WorkSource(this.getWorkSource().getValue()));
		if (this.getWorkTitle() != null) {
			ow.setWorkTitle(this.workTitle.toWorkTitle());
		}
		if (this.getWorkType() != null) {
			ow.setWorkType(WorkType.fromValue(this.getWorkType().getValue()));
		}

		if (this.getJournalTitle() != null) {
			ow.setJournalTitle(new Title(this.getJournalTitle().getValue()));
		}

		if (this.getLanguageCode() != null) {
			ow.setLanguageCode(this.getLanguageCode().getValue());
		}

		if (this.getCountryCode() != null) {
			Country country = new Country(
					StringUtils.isEmpty(this.getCountryCode().getValue()) ? null
							: Iso3166Country.fromValue(this.getCountryCode()
									.getValue()));
			ow.setCountry(country);
		}

		return ow;
	}

	public void setCitationForDisplay(String citation) {
		this.citationForDisplay = citation;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Text getPutCode() {
		return putCode;
	}

	public void setPutCode(Text putCode) {
		this.putCode = putCode;
	}

	public Text getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(Text shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Text getUrl() {
		return url;
	}

	public void setUrl(Text url) {
		this.url = url;
	}

	public Citation getCitation() {
		return citation;
	}

	public void setCitation(Citation citation) {
		this.citation = citation;
	}

	public List<Contributor> getContributors() {
		return contributors;
	}

	public void setContributors(List<Contributor> contributors) {
		this.contributors = contributors;
	}

	public List<WorkExternalIdentifier> getWorkExternalIdentifiers() {
		return workExternalIdentifiers;
	}

	public void setWorkExternalIdentifiers(
			List<WorkExternalIdentifier> workExternalIdentifiers) {
		this.workExternalIdentifiers = workExternalIdentifiers;
	}

	public Text getWorkSource() {
		return workSource;
	}

	public void setWorkSource(Text workSource) {
		this.workSource = workSource;
	}

	public WorkTitle getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(WorkTitle worksTitle) {
		this.workTitle = worksTitle;
	}

	public Text getWorkType() {
		return workType;
	}

	public void setWorkType(Text workType) {
		this.workType = workType;
	}

	public Text getWorkCategory() {
		return workCategory;
	}

	public void setWorkCategory(Text workCategory) {
		this.workCategory = workCategory;
	}

	public Text getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(Text journalTitle) {
		this.journalTitle = journalTitle;
	}

	public Text getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(Text languageCode) {
		this.languageCode = languageCode;
	}

	public Text getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Text countryCode) {
		this.countryCode = countryCode;
	}

	public Text getLanguageName() {
		return languageName;
	}

	public void setLanguageName(Text languageName) {
		this.languageName = languageName;
	}

	public Text getCountryName() {
		return countryName;
	}

	public void setCountryName(Text countryName) {
		this.countryName = countryName;
	}

	public Text getWorkSourceName() {
		return workSourceName;
	}

	public void setWorkSourceName(Text workSourceName) {
		this.workSourceName = workSourceName;
	}
}