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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkCategory;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.record.Work;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
import org.springframework.util.Assert;

public class WorkForm implements ErrorsInterface, Serializable {

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

    private String source;

    private String sourceName;

    private Text title;

    private Text subtitle;

    private TranslatedTitle translatedTitle;

    private Text workCategory;

    private Text workType;

    protected String citationForDisplay;

    private String dateSortString;

    private Date createdDate;

    private Date lastModified;

    public static WorkForm valueOf(Work work) {
        if (work == null)
            return null;

        WorkForm w = new WorkForm();

        // Set work id
        if (work.getPutCode() != null) {
            w.setPutCode(Text.valueOf(work.getPutCode()));
        }

        // Set language
        if (!PojoUtil.isEmpty(work.getLanguageCode())) {
            w.setLanguageCode(Text.valueOf(work.getLanguageCode()));
        }

        // Set type
        if (work.getWorkType() != null) {
            w.setWorkType(Text.valueOf(work.getWorkType().value()));
            // Set category
            WorkCategory category = WorkCategory.fromWorkType(work.getWorkType());
            w.setWorkCategory(Text.valueOf(category.value()));
        }

        if (work.getWorkTitle() != null) {
            // Set title
            if (work.getWorkTitle().getTitle() != null) {
                w.setTitle(Text.valueOf(work.getWorkTitle().getTitle().getContent()));
            }
            // Set translated title
            if (work.getWorkTitle().getTranslatedTitle() != null) {
                TranslatedTitle tt = new TranslatedTitle();
                tt.setContent(work.getWorkTitle().getTranslatedTitle().getContent());
                tt.setLanguageCode(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                w.setTranslatedTitle(tt);
            }
            // Set subtitle
            if (work.getWorkTitle().getSubtitle() != null) {
                w.setSubtitle(Text.valueOf(work.getWorkTitle().getSubtitle().getContent()));
            }
        }

        // Set journal title
        if (work.getJournalTitle() != null ) {
            w.setJournalTitle(Text.valueOf(work.getJournalTitle().getContent()));
        }

        // Set description
        if (work.getShortDescription() != null) {
            w.setShortDescription(Text.valueOf(work.getShortDescription()));
        }

        // Set url
        if (work.getUrl() != null ) {
            w.setUrl(Text.valueOf(work.getUrl().getValue()));
        }

        // Set visibility
        if (work.getVisibility() != null) {
            w.setVisibility(Visibility.fromValue(work.getVisibility().value()));
        }

        // Set country
        if (work.getCountry() != null && work.getCountry().getValue() != null) {
            w.setCountryCode(Text.valueOf(work.getCountry().getValue().value()));
        }

        // Set publication date
        FuzzyDate fuzzyPublicationDate = null;
        if (workEntity.getPublicationDate() != null) {
            PublicationDateEntity publicationDate = workEntity.getPublicationDate();
            fuzzyPublicationDate = new FuzzyDate(publicationDate.getYear(), publicationDate.getMonth(), publicationDate.getDay());
            w.setPublicationDate(Date.valueOf(fuzzyPublicationDate));
        }
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzzyPublicationDate));

        // Set citation
        if (!PojoUtil.isEmpty(workEntity.getCitation())) {
            Citation citation = new Citation();
            citation.setCitation(Text.valueOf(workEntity.getCitation()));
            if (workEntity.getCitationType() != null) {
                citation.setCitationType(Text.valueOf(workEntity.getCitationType().value()));
            }
            w.setCitation(citation);
        }

        // Set contributors
        populateContributors(workEntity, w);

        // Set external identifiers
        populateExternalIdentifiers(workEntity, w);

        // Set created date
        w.setCreatedDate(Date.valueOf(workEntity.getDateCreated()));

        // Set last modified
        w.setLastModified(Date.valueOf(workEntity.getLastModified()));

        // Set source
        w.setSource(workEntity.getSource().getSourceId());
        w.setSourceName(workEntity.getSource().getSourceName());

        return w;
    }

    public static WorkForm valueOf(MinimizedWorkEntity minimizedWorkEntity) {
        WorkForm w = new WorkForm();
        // Set id
        w.setPutCode(Text.valueOf(String.valueOf(minimizedWorkEntity.getId())));
        // Set publication date
        Integer year = (minimizedWorkEntity.getPublicationYear() <= 0) ? null : minimizedWorkEntity.getPublicationYear();
        Integer month = (minimizedWorkEntity.getPublicationMonth() <= 0) ? null : minimizedWorkEntity.getPublicationMonth();
        Integer day = (minimizedWorkEntity.getPublicationDay() <= 0) ? null : minimizedWorkEntity.getPublicationDay();
        FuzzyDate fuzz = new FuzzyDate(year, month, day);
        w.setPublicationDate(Date.valueOf(fuzz));
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzz));

        // Set title and subtitle
        if (!StringUtils.isEmpty(minimizedWorkEntity.getTitle()))
            w.setTitle(Text.valueOf(minimizedWorkEntity.getTitle()));

        if (!StringUtils.isEmpty(minimizedWorkEntity.getTranslatedTitle())) {
            TranslatedTitle translatedTitle = new TranslatedTitle();
            translatedTitle.setContent(minimizedWorkEntity.getTranslatedTitle());
            translatedTitle.setLanguageCode(minimizedWorkEntity.getTranslatedTitleLanguageCode());
            w.setTranslatedTitle(translatedTitle);
        }

        if (!StringUtils.isEmpty(minimizedWorkEntity.getSubtitle()))
            w.setSubtitle(Text.valueOf(minimizedWorkEntity.getSubtitle()));

        // Set Subtitle
        if (!StringUtils.isEmpty(minimizedWorkEntity.getJournalTitle()))
            w.setJournalTitle(Text.valueOf(minimizedWorkEntity.getJournalTitle()));

        // Set description
        if (!StringUtils.isEmpty(minimizedWorkEntity.getDescription())) {
            w.setShortDescription(Text.valueOf(minimizedWorkEntity.getDescription()));
        }
        // Set visibility
        if (minimizedWorkEntity.getVisibility() != null)
            w.setVisibility(minimizedWorkEntity.getVisibility());

        if (minimizedWorkEntity.getWorkType() != null)
            w.setWorkType(Text.valueOf(minimizedWorkEntity.getWorkType().value()));
        WorkExternalIdentifiers identifiers = null;
        if (!StringUtils.isEmpty(minimizedWorkEntity.getExternalIdentifiersJson())) {
            identifiers = JsonUtils.readObjectFromJsonString(minimizedWorkEntity.getExternalIdentifiersJson(), WorkExternalIdentifiers.class);
        }
        populateExternaIdentifiers(identifiers, w);
        if (minimizedWorkEntity.getSource() != null) {
            w.setSource(minimizedWorkEntity.getSource().getSourceId());
            w.setSourceName(minimizedWorkEntity.getSource().getSourceName());
        }
        if (minimizedWorkEntity.getLanguageCode() != null) {
            w.setLanguageCode(Text.valueOf(minimizedWorkEntity.getLanguageCode()));
            w.setLanguageCode(Text.valueOf(minimizedWorkEntity.getLanguageCode()));
        }
        w.setCreatedDate(Date.valueOf(minimizedWorkEntity.getDateCreated()));
        w.setLastModified(Date.valueOf(minimizedWorkEntity.getLastModified()));
        if (minimizedWorkEntity.getWorkUrl() != null)
            w.setUrl(Text.valueOf(minimizedWorkEntity.getWorkUrl()));
        return w;
    }

    public static WorkForm valueOf(OrcidWork orcidWork) {
        WorkForm w = WorkForm.minimizedValueOf(orcidWork);

        // minimized works have everything except citation and contributers now

        if (orcidWork.getWorkContributors() != null && orcidWork.getWorkContributors().getContributor() != null) {
            List<Contributor> contributors = new ArrayList<Contributor>();
            for (org.orcid.jaxb.model.message.Contributor owContributor : orcidWork.getWorkContributors().getContributor()) {
                contributors.add(Contributor.valueOf(owContributor));
            }
            w.setContributors(contributors);
        }

        if (orcidWork.getWorkCitation() != null)
            w.setCitation(Citation.valueOf(orcidWork.getWorkCitation()));

        return w;
    }

    private static void populateExternaIdentifiers(WorkExternalIdentifiers workExternalIdentifiers, WorkForm work) {
        List<WorkExternalIdentifier> workExternalIdentifiersList = new ArrayList<WorkExternalIdentifier>();
        if (workExternalIdentifiers != null && workExternalIdentifiers.getWorkExternalIdentifier() != null)
            for (org.orcid.jaxb.model.message.WorkExternalIdentifier owWorkExternalIdentifier : workExternalIdentifiers.getWorkExternalIdentifier())
                workExternalIdentifiersList.add(WorkExternalIdentifier.valueOf(owWorkExternalIdentifier));
        work.setWorkExternalIdentifiers(workExternalIdentifiersList);
    }

    private static void populateExternalIdentifiers(WorkEntity workEntity, WorkForm work) {
        List<WorkExternalIdentifier> workExternalIdentifiersList = new ArrayList<WorkExternalIdentifier>();
        if (!PojoUtil.isEmpty(workEntity.getExternalIdentifiersJson())) {
            WorkExternalIdentifiers extIds = JsonUtils.readObjectFromJsonString(workEntity.getExternalIdentifiersJson(), WorkExternalIdentifiers.class);
            if (extIds != null) {
                for (org.orcid.jaxb.model.message.WorkExternalIdentifier extId : extIds.getWorkExternalIdentifier()) {
                    workExternalIdentifiersList.add(WorkExternalIdentifier.valueOf(extId));
                }
            }
        }
        work.setWorkExternalIdentifiers(workExternalIdentifiersList);
    }

    private static void populateContributors(WorkEntity workEntity, WorkForm work) {
        List<Contributor> contributorsList = new ArrayList<Contributor>();
        if (!PojoUtil.isEmpty(workEntity.getContributorsJson())) {
            WorkContributors contributors = JsonUtils.readObjectFromJsonString(workEntity.getContributorsJson(), WorkContributors.class);
            if (contributors != null) {
                for (org.orcid.jaxb.model.message.Contributor contributor : contributors.getContributor()) {
                    contributorsList.add(Contributor.valueOf(contributor));
                }
            }
        }
        work.setContributors(contributorsList);
    }

    public static WorkForm minimizedValueOf(OrcidWork orcidWork) {
        WorkForm w = new WorkForm();
        if (orcidWork.getPublicationDate() != null)
            w.setPublicationDate(Date.valueOf(orcidWork.getPublicationDate()));
        w.setDateSortString(PojoUtil.createDateSortString(null, orcidWork.getPublicationDate()));
        if (orcidWork.getPutCode() != null)
            w.setPutCode(Text.valueOf(orcidWork.getPutCode()));
        if (orcidWork.getShortDescription() != null)
            w.setShortDescription(Text.valueOf(orcidWork.getShortDescription()));
        if (orcidWork.getUrl() != null)
            w.setUrl(Text.valueOf(orcidWork.getUrl().getValue()));
        if (orcidWork.getVisibility() != null)
            w.setVisibility(orcidWork.getVisibility());
        WorkExternalIdentifiers workExternalIdentifiers = null;
        if (orcidWork.getWorkExternalIdentifiers() != null) {
            workExternalIdentifiers = orcidWork.getWorkExternalIdentifiers();
        }
        populateExternaIdentifiers(workExternalIdentifiers, w);
        if (orcidWork.getSource() != null) {
            w.setSource(orcidWork.getSource().retrieveSourcePath());
            if (orcidWork.getSource().getSourceName() != null)
                w.setSourceName(orcidWork.getSource().getSourceName().getContent());
        }

        WorkTitle workTitle = orcidWork.getWorkTitle();
        if (workTitle == null)
            workTitle = new WorkTitle();
        if (workTitle.getTitle() != null) {
            w.setTitle(Text.valueOf(workTitle.getTitle().getContent()));
        }
        if (workTitle.getSubtitle() != null) {
            w.setSubtitle(Text.valueOf(workTitle.getSubtitle().getContent()));
        }
        if (workTitle.getTranslatedTitle() != null) {
            TranslatedTitle translatedTitle = new TranslatedTitle();
            translatedTitle.setContent((workTitle.getTranslatedTitle() == null) ? null : workTitle.getTranslatedTitle().getContent());
            translatedTitle.setLanguageCode((workTitle.getTranslatedTitle() == null || workTitle.getTranslatedTitle().getLanguageCode() == null) ? null : workTitle
                    .getTranslatedTitle().getLanguageCode());
            w.setTranslatedTitle(translatedTitle);
        }

        if (orcidWork.getWorkType() != null) {
            w.setWorkType(Text.valueOf(orcidWork.getWorkType().value()));
            WorkCategory category = WorkCategory.fromWorkType(orcidWork.getWorkType());
            w.setWorkCategory(Text.valueOf(category.value()));
        }

        if (orcidWork.getJournalTitle() != null)
            w.setJournalTitle(Text.valueOf(orcidWork.getJournalTitle().getContent()));

        if (orcidWork.getLanguageCode() != null)
            w.setLanguageCode(Text.valueOf(orcidWork.getLanguageCode()));

        if (orcidWork.getCountry() != null)
            w.setCountryCode((orcidWork.getCountry().getValue() == null) ? null : Text.valueOf(orcidWork.getCountry().getValue().value()));
        w.setCreatedDate(Date.valueOf(orcidWork.getCreatedDate()));
        w.setLastModified(Date.valueOf(orcidWork.getLastModifiedDate()));
        return w;
    }

    public OrcidWork toOrcidWork() {
        OrcidWork ow = new OrcidWork();
        if (this.getPublicationDate() != null)
            ow.setPublicationDate(new PublicationDate(this.getPublicationDate().toFuzzyDate()));
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
        List<org.orcid.jaxb.model.message.WorkExternalIdentifier> wiList = new ArrayList<org.orcid.jaxb.model.message.WorkExternalIdentifier>();
        if (this.getWorkExternalIdentifiers() != null) {
            for (WorkExternalIdentifier wi : this.getWorkExternalIdentifiers()) {
                wiList.add(wi.toWorkExternalIdentifier());
            }
        }
        ow.setWorkExternalIdentifiers(new WorkExternalIdentifiers(wiList));
        if (this.getSource() != null)
            ow.setSource(new Source(this.getSource()));

        if (this.getTitle() != null || this.getSubtitle() != null || this.getTranslatedTitle() != null)
            ow.setWorkTitle(new WorkTitle());
        if (this.getTitle() != null)
            ow.getWorkTitle().setTitle(this.getTitle().toTitle());
        if (this.getSubtitle() != null)
            ow.getWorkTitle().setSubtitle(this.getSubtitle().toSubtitle());
        if (this.getTranslatedTitle() != null)
            ow.getWorkTitle().setTranslatedTitle(this.getTranslatedTitle().toTranslatedTitle());

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
            Country country = new Country(StringUtils.isEmpty(this.getCountryCode().getValue()) ? null : Iso3166Country.fromValue(this.getCountryCode().getValue()));
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

    public void setWorkExternalIdentifiers(List<WorkExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDateSortString() {
        return dateSortString;
    }

    public void setDateSortString(String dateSortString) {
        this.dateSortString = dateSortString;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

    public TranslatedTitle getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitle translatedTitle) {
        this.translatedTitle = translatedTitle;
    }
}