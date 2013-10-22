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
package org.orcid.frontend.web.forms;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.jbibtex.ParseException;
import org.orcid.core.crossref.CrossRefContext;
import org.orcid.core.crossref.CrossRefMetadata;
import org.orcid.frontend.web.forms.validate.ValidCurrentWorkBibtex;
import org.orcid.frontend.web.forms.validate.ValidCurrentWorkExternalId;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.NewWorkType;
import org.orcid.jaxb.model.message.Year;
import org.orcid.utils.BibtexUtils;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AutoPopulatingList;
import org.springframework.web.util.HtmlUtils;

@ValidCurrentWorkBibtex
public class CurrentWork {

    private static Logger LOGGER = LoggerFactory.getLogger(CurrentWork.class);

    private String citation;

    private String citationType = CitationType.FORMATTED_UNSPECIFIED.value();

    private String visibility;

    private String title;

    private String translatedTitle;

    private String translatedTitleLanguageCode;

    private String journalTitle;

    private String subtitle;

    private String workType;

    private String year;

    private String month;

    private String day;

    private String url;

    private String description;

    private String putCode = "-1";

    private AutoPopulatingList<CurrentWorkContributor> currentWorkContributors;

    private AutoPopulatingList<CurrentWorkExternalId> currentWorkExternalIds;

    private String coins;

    private CrossRefContext crossRefContext;

    private boolean selected;

    private String source;

    private String languageCode;

    private String country;

    public CurrentWork() {
    }

    public CurrentWork(OrcidWork orcidWork) {
        setCitationDetails(orcidWork);

        Visibility orcidWorkVisibility = orcidWork.getVisibility();
        if (orcidWorkVisibility == null) {
            visibility = Visibility.PRIVATE.value();
        } else {
            visibility = orcidWorkVisibility.value();
        }
        if (StringUtils.isNotBlank(orcidWork.getPutCode())) {
            putCode = orcidWork.getPutCode();
        }
        WorkTitle workTitle = orcidWork.getWorkTitle();
        if (workTitle != null) {
            title = workTitle.getTitle() == null ? null : workTitle.getTitle().getContent();
            subtitle = workTitle.getSubtitle() == null ? null : workTitle.getSubtitle().getContent();
            TranslatedTitle orcidTranslatedTitle = workTitle.getTranslatedTitle();
            if (orcidTranslatedTitle != null) {
                translatedTitle = (orcidTranslatedTitle.getContent() == null) ? null : orcidTranslatedTitle.getContent();
                translatedTitleLanguageCode = (orcidTranslatedTitle.getLanguageCode() == null) ? null : orcidTranslatedTitle.getLanguageCode();
            }
        }

        Title orcidJournalTitle = orcidWork.getJournalTitle();
        if (orcidJournalTitle != null) {
            journalTitle = orcidJournalTitle.getContent();
        }

        WorkType orcidWorkType = orcidWork.getWorkType();
        if (orcidWorkType != null) {
            workType = orcidWorkType.value();
        }
        description = orcidWork.getShortDescription();
        PublicationDate publicationDate = orcidWork.getPublicationDate();
        if (publicationDate != null) {
            year = publicationDate.getYear() == null ? null : publicationDate.getYear().getValue();
            month = publicationDate.getMonth() == null ? null : publicationDate.getMonth().getValue();
            day = publicationDate.getDay() == null ? null : publicationDate.getDay().getValue();
        }
        WorkContributors workContributors = orcidWork.getWorkContributors();
        if (workContributors != null) {
            List<CurrentWorkContributor> currentWorkContributors = new ArrayList<CurrentWorkContributor>();
            for (Contributor contributor : workContributors.getContributor()) {
                currentWorkContributors.add(new CurrentWorkContributor(contributor));
            }
            setCurrentWorkContributors(currentWorkContributors);
        }
        Url orcidWorkUrl = orcidWork.getUrl();
        if (orcidWorkUrl != null) {
            url = orcidWorkUrl.getValue();
        }
        WorkExternalIdentifiers workExternalIdentifiers = orcidWork.getWorkExternalIdentifiers();
        if (workExternalIdentifiers != null) {
            List<CurrentWorkExternalId> currentWorkExternalIds = new ArrayList<CurrentWorkExternalId>();
            for (WorkExternalIdentifier workExternalIdentifier : workExternalIdentifiers.getWorkExternalIdentifier()) {
                currentWorkExternalIds.add(new CurrentWorkExternalId(workExternalIdentifier));
            }
            setCurrentWorkExternalIds(currentWorkExternalIds);
        }

        if (orcidWork.getWorkSource() != null)
            source = orcidWork.getWorkSource().getContent();

        languageCode = orcidWork.getLanguageCode();

        if (orcidWork.getCountry() != null && orcidWork.getCountry().getValue() != null)
            country = orcidWork.getCountry().getValue().value();
    }

    private void setCitationDetails(OrcidWork orcidWork) {
        Citation workCitation = orcidWork != null ? orcidWork.getWorkCitation() : null;
        if (workCitation != null && StringUtils.isNotBlank(workCitation.getCitation())) {
            citationToString(workCitation);
        }
    }

    public CurrentWork(CrossRefMetadata metadata) {
        setDoi(metadata);
        citation = metadata.getFullCitation();
        title = metadata.getTitle();
        coins = metadata.getCoins();
        crossRefContext = new CrossRefContext(coins);
        crossRefContext.parse();
        setDate();
        setContributor();
    }

    private void setDoi(CrossRefMetadata metadata) {
        currentWorkExternalIds = new AutoPopulatingList<CurrentWorkExternalId>(CurrentWorkExternalId.class);
        CurrentWorkExternalId extId = new CurrentWorkExternalId();
        currentWorkExternalIds.add(extId);
        extId.setType(WorkExternalIdentifierType.DOI.value());
        extId.setId(metadata.getDoi());
    }

    private void setDate() {
        String dateString = crossRefContext.getDate();
        XMLGregorianCalendar cal = DateUtils.convertToXMLGregorianCalendar(dateString);
        if (cal != null) {
            int calYear = cal.getYear();
            if (calYear != DatatypeConstants.FIELD_UNDEFINED) {
                year = String.valueOf(calYear);
            }
            int calMonth = cal.getMonth();
            if (calMonth != DatatypeConstants.FIELD_UNDEFINED) {
                month = String.valueOf(calMonth);
            }
            int calDay = cal.getDay();
            if (calDay != DatatypeConstants.FIELD_UNDEFINED) {
                day = String.valueOf(calDay);
            }
        }
    }

    private void setContributor() {
        currentWorkContributors = new AutoPopulatingList<CurrentWorkContributor>(CurrentWorkContributor.class);
        CurrentWorkContributor contributor = new CurrentWorkContributor();
        currentWorkContributors.add(contributor);
        contributor.setRole(ContributorRole.AUTHOR.value());
        contributor.setSequence(SequenceType.FIRST.value());
        contributor.setCreditName(crossRefContext.getAuthor());
    }

    public String getCitation() {
        return citation;
    }

    public String getCitationForDisplay() {
        if (CitationType.BIBTEX.value().toLowerCase().equals(citationType)) {
            try {
                return BibtexUtils.toCitation(HtmlUtils.htmlUnescape(citation));
            } catch (ParseException e) {
                LOGGER.info("Invalid BibTeX. Sending back as a string");
            }
        }
        if (StringUtils.isNotBlank(citation)) {
            return citation;
        }
        return null;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getCitationType() {
        return citationType;
    }

    public void setCitationType(String citationType) {
        this.citationType = citationType;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @NotBlank
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

    @NotBlank
    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Valid
    public List<CurrentWorkContributor> getCurrentWorkContributors() {
        return currentWorkContributors;
    }

    public void setCurrentWorkContributors(List<CurrentWorkContributor> currentWorkContributors) {
        this.currentWorkContributors = new AutoPopulatingList<CurrentWorkContributor>(currentWorkContributors, CurrentWorkContributor.class);
    }

    @ValidCurrentWorkExternalId(message = "If an ID is specified, the type is required")
    public List<CurrentWorkExternalId> getCurrentWorkExternalIds() {
        return currentWorkExternalIds;
    }

    public void setCurrentWorkExternalIds(List<CurrentWorkExternalId> currentWorkExternalIds) {
        this.currentWorkExternalIds = new AutoPopulatingList<CurrentWorkExternalId>(CurrentWorkExternalId.class);
        if (currentWorkExternalIds != null && !currentWorkExternalIds.isEmpty()) {
            this.currentWorkExternalIds.addAll(currentWorkExternalIds);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public OrcidWork getOrcidWork() {
        OrcidWork orcidWork = new OrcidWork();
        Citation workCitation = getConvertedCitation();
        orcidWork.setWorkCitation(workCitation);
        if (StringUtils.isNotBlank(visibility)) {
            orcidWork.setVisibility(Visibility.fromValue(visibility));
        }
        if (StringUtils.isNotBlank(title)) {
            WorkTitle workTitle = new WorkTitle();
            orcidWork.setWorkTitle(workTitle);
            workTitle.setTitle(new Title(title));
        }

        if (StringUtils.isNotBlank(translatedTitle)) {
            WorkTitle workTitle = orcidWork.getWorkTitle();
            if (workTitle == null) {
                workTitle = new WorkTitle();
                orcidWork.setWorkTitle(workTitle);
            }
            TranslatedTitle translatedTitle = new TranslatedTitle();
            translatedTitle.setContent(this.translatedTitle);
            translatedTitle.setLanguageCode(StringUtils.isBlank(this.translatedTitleLanguageCode) ? null : this.translatedTitleLanguageCode);
            workTitle.setTranslatedTitle(translatedTitle);
        }

        if (StringUtils.isNotBlank(subtitle)) {
            WorkTitle workTitle = orcidWork.getWorkTitle();
            if (workTitle == null) {
                workTitle = new WorkTitle();
                orcidWork.setWorkTitle(workTitle);
            }
            workTitle.setSubtitle(new Subtitle(subtitle));
        }

        if (StringUtils.isNotBlank(journalTitle)) {
            orcidWork.setJournalTitle(new Title(journalTitle));
        }
        if (StringUtils.isNotBlank(description)) {
            orcidWork.setShortDescription(description);
        }
        if (StringUtils.isNotBlank(workType)) {
            WorkType orcidWorkType = WorkType.fromValue(workType);
            orcidWork.setWorkType(orcidWorkType);
        }
        if (StringUtils.isNotBlank(year)) {
            PublicationDate publicationDate = retrievePublicationDate(orcidWork);
            publicationDate.setYear(new Year(Integer.valueOf(year)));
        }
        if (StringUtils.isNotBlank(month)) {
            PublicationDate publicationDate = retrievePublicationDate(orcidWork);
            publicationDate.setMonth(new Month(Integer.valueOf(month)));
        }
        if (StringUtils.isNotBlank(day)) {
            PublicationDate publicationDate = retrievePublicationDate(orcidWork);
            publicationDate.setDay(new Day(Integer.valueOf(day)));
        }
        if (currentWorkExternalIds != null && !currentWorkExternalIds.isEmpty()) {
            WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
            orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
            for (CurrentWorkExternalId extId : currentWorkExternalIds) {
                if (extId.isValid()) {
                    workExternalIdentifiers.getWorkExternalIdentifier().add(extId.getWorkExternalIdentifier());
                }
            }
        }
        if (StringUtils.isNotBlank(url)) {
            orcidWork.setUrl(new Url(url));
        }
        if (currentWorkContributors != null && !currentWorkContributors.isEmpty()) {
            WorkContributors workContributors = new WorkContributors();
            orcidWork.setWorkContributors(workContributors);
            for (CurrentWorkContributor currentWorkContributor : currentWorkContributors) {
                workContributors.getContributor().add(currentWorkContributor.getContributor());
            }
        }

        if (StringUtils.isNotBlank(this.languageCode)) {
            orcidWork.setLanguageCode(this.languageCode);
        }

        if (StringUtils.isNotBlank(putCode)) {
            orcidWork.setPutCode(putCode);
        }

        if (StringUtils.isNotBlank(source)) {
            orcidWork.setWorkSource(new WorkSource(source));
        }

        if (StringUtils.isNotBlank(country)) {
            Country owCountry = new Country(Iso3166Country.fromValue(country));
            orcidWork.setCountry(owCountry);
        }

        return orcidWork;
    }

    PublicationDate retrievePublicationDate(OrcidWork orcidWork) {
        PublicationDate publicationDate = orcidWork.getPublicationDate();
        if (publicationDate == null) {
            publicationDate = new PublicationDate();
            orcidWork.setPublicationDate(publicationDate);
        }
        return publicationDate;
    }

    public OrcidProfile getOrcidProfile(String orcid) {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid(orcid);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        orcidWorks.getOrcidWork().add(getOrcidWork());
        return orcidProfile;
    }

    public boolean hasRequiredFormFields() {
        return !(StringUtils.isBlank(workType) && StringUtils.isBlank(title) && StringUtils.isBlank(citation) && StringUtils.isBlank(visibility));
    }

    private Citation getConvertedCitation() {
        if (CitationType.BIBTEX.value().equals(citationType)) {
            return new Citation(HtmlUtils.htmlUnescape(citation), CitationType.fromValue(citationType));
        } else {
            try {
                CitationType.fromValue(citationType);
            } catch (IllegalArgumentException e) {
                citationType = CitationType.FORMATTED_UNSPECIFIED.value();
            }
            return new Citation(citation, CitationType.fromValue(citationType));
        }

    }

    private void citationToString(Citation workCitation) {
        if (CitationType.BIBTEX.equals(workCitation.getWorkCitationType())) {
            if (BibtexUtils.isValid(workCitation.getCitation())) {
                citation = HtmlUtils.htmlEscape(workCitation.getCitation());
                citationType = CitationType.BIBTEX.value();
            } else {
                citation = "";
                citationType = CitationType.FORMATTED_UNSPECIFIED.value();
            }
        } else {
            citation = workCitation.getCitation();
            citationType = (workCitation.getWorkCitationType() != null && !CitationType.BIBTEX.equals(workCitation.getWorkCitationType())) ? workCitation
                    .getWorkCitationType().value() : CitationType.FORMATTED_UNSPECIFIED.value();
        }
    }

}
