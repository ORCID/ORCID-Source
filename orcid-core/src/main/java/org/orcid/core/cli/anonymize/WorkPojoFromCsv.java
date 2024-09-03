package org.orcid.core.cli.anonymize;

import java.net.MalformedURLException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkPojoFromCsv {
    private static final long serialVersionUID = 1L;

    /*
     * 
     * work_id,date_created,last_modified,publication_day,publication_month,
     * publication_year,title,subtitle,description,work_url,citation,work_type,
     * citation_type,contributors_json,journal_title,language_code,
     * translated_title,translated_title_language_code,iso2_country,
     * external_ids_json,orcid,added_to_profile_date,visibility,display_index,
     * source_id,client_source_id,assertion_origin_source_id,
     * assertion_origin_client_source_id, top_contributors_json
     */

    @JsonProperty("work_id")
    protected Long id;
    protected String title;
    @JsonProperty("translated_title")
    protected String translatedTitle;
    protected String subtitle;
    protected String citation;
    @JsonProperty("citation_type")
    protected String citationType;
    protected String description;
    @JsonProperty("work_url")
    protected String workUrl;
    @JsonProperty("journal_title")
    protected String journalTitle;
    @JsonProperty("language_code")
    protected String languageCode;
    @JsonProperty("translated_title_language_code")
    protected String translatedTitleLanguageCode;
    @JsonProperty("work_type")
    protected String workType;
    @JsonProperty("publication_day")
    protected Integer publicationDay;
    @JsonProperty("publication_month")
    protected Integer publicationMonth;
    @JsonProperty("publication_year")
    protected Integer publicationYear;
    @JsonProperty("contributors_json")
    protected String contributorsJson;
    @JsonProperty("top_contributors_json")
    protected String topContributorsJson;
    @JsonProperty("external_ids_json")
    protected String externalIdentifiersJson;
    protected String visibility;
    @JsonProperty("display_index")
    protected Long displayIndex;
    @JsonProperty("date_created")
    protected String dateCreated;
    @JsonProperty("last_modified")
    protected String lastModified;
    @JsonProperty("iso2_country")
    protected String iso2Country;
    protected String orcid;
    @JsonProperty("added_to_profile_date")
    protected String addedToProfileDate;
    @JsonProperty("source_id")
    protected String sourceId;
    @JsonProperty("client_source_id")
    protected String clientSourceId;
    @JsonProperty("assertion_origin_source_id")
    protected String assertionOriginSourceId;
    @JsonProperty("assertion_origin_client_source_id")
    protected String assertionOriginClientSourceId;
    

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public Integer getPublicationDay() {
        return publicationDay;
    }

    public void setPublicationDay(Integer publicationDay) {
        this.publicationDay = publicationDay;
    }

    public Integer getPublicationMonth() {
        return publicationMonth;
    }

    public void setPublicationMonth(Integer publicationMonth) {
        this.publicationMonth = publicationMonth;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    public String getTopContributorsJson() {
        return topContributorsJson;
    }

    public void setTopContributorsJson(String topContributorsJson) {
        this.topContributorsJson = topContributorsJson;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the titles
     */

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

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

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

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }

    public String getCitationType() {
        return citationType;
    }

    public void setCitationType(String citationType) {
        this.citationType = citationType;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(String iso2Country) {
        this.iso2Country = iso2Country;
    }

    public String getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(String addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getClientSourceId() {
        return clientSourceId;
    }

    public void setClientSourceId(String clientSourceId) {
        this.clientSourceId = clientSourceId;
    }

    public String getAssertionOriginSourceId() {
        return assertionOriginSourceId;
    }

    public void setAssertionOriginSourceId(String assertionOriginSourceId) {
        this.assertionOriginSourceId = assertionOriginSourceId;
    }

    public String getAssertionOriginClientSourceId() {
        return assertionOriginClientSourceId;
    }

    public void setAssertionOriginClientSourceId(String assertionOriginClientSourceId) {
        this.assertionOriginClientSourceId = assertionOriginClientSourceId;
    }

    public Work toAnonymizedWork(String orcid) throws MalformedURLException, JSONException {
        Work work = new Work();
        AnonymizeText anonymizeText = new AnonymizeText();

        // Set language
        if (!PojoUtil.isEmpty(this.getLanguageCode())) {
            work.setLanguageCode(this.getLanguageCode());
        }

        // Set type
        if (!PojoUtil.isEmpty(this.getWorkType())) {
            work.setWorkType(WorkType.valueOf(this.getWorkType()));
        }

        org.orcid.jaxb.model.v3.release.record.WorkTitle workTitle = new org.orcid.jaxb.model.v3.release.record.WorkTitle();
        // Set title
        if (!PojoUtil.isEmpty(this.getTitle())) {
            workTitle.setTitle(new org.orcid.jaxb.model.v3.release.common.Title(anonymizeText.anonymizeString(this.getTitle())));
        }

        // Set translated title
        if (this.getTranslatedTitle() != null && !PojoUtil.isEmpty(this.getTranslatedTitle())) {
            org.orcid.jaxb.model.v3.release.common.TranslatedTitle translatedTitle = new org.orcid.jaxb.model.v3.release.common.TranslatedTitle();
            translatedTitle.setContent(anonymizeText.anonymizeString(this.getTranslatedTitle()));
            translatedTitle.setLanguageCode(this.getTranslatedTitleLanguageCode());
            workTitle.setTranslatedTitle(translatedTitle);
        }

        // Set subtitle
        if (!PojoUtil.isEmpty(this.getSubtitle())) {
            org.orcid.jaxb.model.v3.release.common.Subtitle subtitle = new org.orcid.jaxb.model.v3.release.common.Subtitle();
            subtitle.setContent(anonymizeText.anonymizeString(this.getSubtitle()));
            workTitle.setSubtitle(subtitle);
        }

        work.setWorkTitle(workTitle);

        // Set journal title
        if (!PojoUtil.isEmpty(this.getJournalTitle())) {
            work.setJournalTitle(new org.orcid.jaxb.model.v3.release.common.Title(anonymizeText.anonymizeString(this.getJournalTitle())));
        }

        // Set description
        if (!PojoUtil.isEmpty(this.getDescription())) {
            work.setShortDescription(anonymizeText.anonymizeString(this.getDescription()));
        }

        // Set url
        if (!PojoUtil.isEmpty(this.getWorkUrl())) {
            work.setUrl(new Url(anonymizeText.anonymizeString(this.getWorkUrl())));
        } else {
            work.setUrl(new Url());
        }

        // Set visibility
        if (this.getVisibility() != null && this.getVisibility() != null) {
            work.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(this.getVisibility()));
        }

        // Set country
        if (!PojoUtil.isEmpty(this.getIso2Country())) {
            work.setCountry(new org.orcid.jaxb.model.v3.release.common.Country(org.orcid.jaxb.model.common.Iso3166Country.fromValue(this.getIso2Country())));
        }

        // Set publication date
        Integer yearInteger = this.getPublicationYear() != null && this.getPublicationYear() > 0 ? Integer.valueOf(this.getPublicationYear()) : null;
        Integer monthInteger = this.getPublicationMonth() != null && this.getPublicationMonth() > 0 ? Integer.valueOf(this.getPublicationMonth()) : null;
        Integer dayInteger = this.getPublicationDay() != null && this.getPublicationDay() > 0 ? Integer.valueOf(this.getPublicationDay()) : null;
        org.orcid.jaxb.model.v3.release.common.Year year = null;
        org.orcid.jaxb.model.v3.release.common.Month month = null;
        org.orcid.jaxb.model.v3.release.common.Day day = null;
        if (yearInteger != null) {
            year = new org.orcid.jaxb.model.v3.release.common.Year(yearInteger);
        }
        if (monthInteger != null) {
            month = new org.orcid.jaxb.model.v3.release.common.Month(monthInteger);
        }
        if (dayInteger != null) {
            day = new org.orcid.jaxb.model.v3.release.common.Day(dayInteger);
        }
        work.setPublicationDate(new org.orcid.jaxb.model.v3.release.common.PublicationDate(year, month, day));

        // Set citation
        if (this.getCitation() != null) {
            org.orcid.jaxb.model.v3.release.record.Citation citation = new org.orcid.jaxb.model.v3.release.record.Citation();
            if (!PojoUtil.isEmpty(this.getCitation())) {
                citation.setCitation(anonymizeText.anonymizeString(this.getCitation()));
            }

            if (!PojoUtil.isEmpty(this.getCitationType())) {
                citation.setWorkCitationType(CitationType.valueOf(this.getCitationType()));
            }
            work.setWorkCitation(citation);
        }

        // Set contributors
        if (this.getContributorsJson() != null && !PojoUtil.isEmpty(this.getContributorsJson())) {
            JSONObject contrJson = new JSONObject(this.getContributorsJson());
            if (contrJson.has("contributor")) {
                work.setWorkContributors(anonymizeText.anonymizeWorkContributors(contrJson.getJSONArray("contributor")));
            }

        }

        // Set externalids
        if (this.getExternalIdentifiersJson() != null && !PojoUtil.isEmpty(this.getExternalIdentifiersJson())) {
            JSONObject extIdentifiersJson = new JSONObject(this.getExternalIdentifiersJson());
            boolean haveExternalIdentifiers = false;
            if (extIdentifiersJson.has("workExternalIdentifier")) {
                ExternalIDs extIds = anonymizeText.anonymizeWorkExternalIdentifiers(extIdentifiersJson.getJSONArray("workExternalIdentifier"));
                if(extIds != null && !extIds.getExternalIdentifier().isEmpty()) {
                    work.setWorkExternalIdentifiers(extIds);
                    haveExternalIdentifiers = true;
                }

            }
            if(!haveExternalIdentifiers) {
                work.setWorkExternalIdentifiers(new ExternalIDs());
            }
        }

        // Set created date
        if (!PojoUtil.isEmpty(this.getDateCreated())) {
            CreatedDate createdDate = new CreatedDate();
            createdDate.setValue(DateUtils.convertToXMLGregorianCalendar(this.getDateCreated()));
            work.setCreatedDate(createdDate);
        }

        // Set last modified
        if (!PojoUtil.isEmpty(this.getLastModified())) {
            org.orcid.jaxb.model.v3.release.common.LastModifiedDate lastModified = new org.orcid.jaxb.model.v3.release.common.LastModifiedDate();
            lastModified.setValue(DateUtils.convertToXMLGregorianCalendar(this.getLastModified()));
            work.setLastModifiedDate(lastModified);
        }

        // TODO pass source!!!
        org.orcid.jaxb.model.v3.release.common.Source source = new org.orcid.jaxb.model.v3.release.common.Source();
        SourceOrcid srcOrcid = new SourceOrcid(orcid);
        srcOrcid.setPath(orcid);
        source.setSourceOrcid(srcOrcid);
        work.setSource(source);

        return work;
    }

}
