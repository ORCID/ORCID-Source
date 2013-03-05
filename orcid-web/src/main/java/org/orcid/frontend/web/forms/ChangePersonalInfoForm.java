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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.AlternativeEmails;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Visibility;

public class ChangePersonalInfoForm {

    private static final String DELIMITER = ";";

    private String firstName;
    private String lastName;
    private String creditName;
    private String creditNameVisibility;
    private String otherNamesDelimited;
    private String otherNamesVisibility;
    private String biography;

    private String keywordsDelimited;
    private String keywordsVisibility;

    private String isoCountryCode;
    private String isoCountryVisibility;

    private String websiteUrl;
    private String websiteUrlText;
    private String websiteUrlVisibility;

    private ExternalIdentifiers externalIdentifiers;
    private ResearcherUrls savedResearcherUrls;

    private String email;
    private String emailVisibility;

    private boolean emailVerified;

    private AlternativeEmails savedAlternativeEmails;

    public ChangePersonalInfoForm() {
    }

    public ChangePersonalInfoForm(OrcidProfile orcidProfile) {
        initNullSafeValues(orcidProfile);
        setCreditName(orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        setCreditNameVisibility(orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility().value());
        setFirstName(orcidProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        setLastName(orcidProfile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        setOtherNamesDelimited(buildOtherNamesAsDelimitedString(orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames()));
        setOtherNamesVisibility(orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility().value());
        setKeywordsDelimited(buildKeywordsAsDelimitedString(orcidProfile.getOrcidBio().getKeywords()));
        setKeywordsVisibility(orcidProfile.getOrcidBio().getKeywords().getVisibility().value());
        setBiography(orcidProfile.getOrcidBio().getBiography().getContent());
        setIsoCountryCode(orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getContent());
        setIsoCountryVisibility(orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility().value());
        setSavedResearcherUrls(orcidProfile.getOrcidBio().getResearcherUrls());
        setWebsiteUrlVisibility(orcidProfile.getOrcidBio().getResearcherUrls().getVisibility().value());
        setExternalIdentifiers(orcidProfile.getOrcidBio().getExternalIdentifiers());
        setEmail(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        setEmailVisibility(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getVisibility().value());
        setEmailVerified(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());
    }

    private void initNullSafeValues(OrcidProfile currentOrcidProfile) {
        if (currentOrcidProfile.getOrcidBio() == null) {
            currentOrcidProfile.setOrcidBio(new OrcidBio());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails() == null) {
            currentOrcidProfile.getOrcidBio().setContactDetails(new ContactDetails());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() == null) {
            currentOrcidProfile.getOrcidBio().getContactDetails().addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setVisibility(OrcidVisibilityDefaults.PRIMARY_EMAIL_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails() == null) {
            currentOrcidProfile.getOrcidBio().setPersonalDetails(new PersonalDetails());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails().getCreditName() == null) {
            currentOrcidProfile.getOrcidBio().getPersonalDetails().setCreditName(new CreditName());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getPersonalDetails().getCreditName().setVisibility(OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails().getFamilyName() == null) {
            currentOrcidProfile.getOrcidBio().getPersonalDetails().setFamilyName(new FamilyName());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails().getGivenNames() == null) {
            currentOrcidProfile.getOrcidBio().getPersonalDetails().setGivenNames(new GivenNames());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails().getOtherNames() == null) {
            currentOrcidProfile.getOrcidBio().getPersonalDetails().setOtherNames(new OtherNames());
        }

        if (currentOrcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidBio().getExternalIdentifiers() == null) {
            currentOrcidProfile.getOrcidBio().setExternalIdentifiers(new ExternalIdentifiers());
        }

        if (currentOrcidProfile.getOrcidBio().getExternalIdentifiers().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getExternalIdentifiers().setVisibility(OrcidVisibilityDefaults.EXTERNAL_IDENTIFIER_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidBio().getResearcherUrls() == null) {
            currentOrcidProfile.getOrcidBio().setResearcherUrls(new ResearcherUrls());
        }

        if (currentOrcidProfile.getOrcidBio().getResearcherUrls().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getResearcherUrls().setVisibility(OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidBio().getKeywords() == null) {
            currentOrcidProfile.getOrcidBio().setKeywords(new Keywords());
        }

        if (currentOrcidProfile.getOrcidBio().getKeywords().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getKeywords().setVisibility(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidBio().getBiography() == null) {
            currentOrcidProfile.getOrcidBio().setBiography(new Biography());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails() == null) {
            currentOrcidProfile.getOrcidBio().setContactDetails(new ContactDetails());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails().getAddress() == null) {
            currentOrcidProfile.getOrcidBio().getContactDetails().setAddress(new Address());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry() == null) {
            currentOrcidProfile.getOrcidBio().getContactDetails().getAddress().setCountry(new Country());
        }

        if (currentOrcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility() == null) {
            currentOrcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().setVisibility(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility());
        }

        if (currentOrcidProfile.getOrcidHistory() == null) {
            currentOrcidProfile.setOrcidHistory(new OrcidHistory());
        }

    }

    @NotBlank(message = "Please enter your first name.")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    @NotBlank(message = "Please enter your e-mail address.")
    @Email(message = "Invalid email address.")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVisibility() {
        return emailVisibility;
    }

    public void setEmailVisibility(String emailVisibility) {
        this.emailVisibility = emailVisibility;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreditNameVisibility() {
        return creditNameVisibility;
    }

    public void setCreditNameVisibility(String creditNameVisibility) {
        this.creditNameVisibility = creditNameVisibility;
    }

    public String getOtherNames() {
        return otherNamesDelimited;
    }

    public void setOtherNames(String otherNames) {
        this.otherNamesDelimited = otherNames;
    }

    public String getOtherNamesDelimited() {
        return otherNamesDelimited;
    }

    public void setOtherNamesDelimited(String otherNamesDelimited) {
        this.otherNamesDelimited = otherNamesDelimited;
    }

    public String getKeywordsDelimited() {
        return keywordsDelimited;
    }

    public void setKeywordsDelimited(String keywordsDelimited) {
        this.keywordsDelimited = keywordsDelimited;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public void setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteUrlText() {
        return websiteUrlText;
    }

    public void setWebsiteUrlText(String websiteUrlText) {
        this.websiteUrlText = websiteUrlText;
    }

    public ResearcherUrls getSavedResearcherUrls() {
        return savedResearcherUrls;
    }

    public void setSavedResearcherUrls(ResearcherUrls savedResearcherUrls) {
        this.savedResearcherUrls = savedResearcherUrls;
    }

    public AlternativeEmails getSavedAlternativeEmails() {
        return savedAlternativeEmails;
    }

    public void setSavedAlternativeEmails(AlternativeEmails savedAlternativeEmails) {
        this.savedAlternativeEmails = savedAlternativeEmails;
    }

    public String getOtherNamesVisibility() {
        return otherNamesVisibility;
    }

    public void setOtherNamesVisibility(String otherNamesVisibility) {
        this.otherNamesVisibility = otherNamesVisibility;
    }

    public String getKeywordsVisibility() {
        return keywordsVisibility;
    }

    public void setKeywordsVisibility(String keywordsVisibility) {
        this.keywordsVisibility = keywordsVisibility;
    }

    @Length(max = 1000, message = "The maximum length for biography is 1000 characters, including line breaks")
    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getWebsiteUrlVisibility() {
        return websiteUrlVisibility;
    }

    public void setWebsiteUrlVisibility(String websiteUrlVisibility) {
        this.websiteUrlVisibility = websiteUrlVisibility;
    }

    public String getIsoCountryVisibility() {
        return isoCountryVisibility;
    }

    public void setIsoCountryVisibility(String isoCountryVisibility) {
        this.isoCountryVisibility = isoCountryVisibility;
    }

    public ExternalIdentifiers getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIdentifiers externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    private void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void mergeOrcidBioDetails(OrcidProfile orcidProfile) {
        initNullSafeValues(orcidProfile);
        orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().setContent(creditName);
        orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().setVisibility(Visibility.fromValue(creditNameVisibility));
        orcidProfile.getOrcidBio().getPersonalDetails().getGivenNames().setContent(firstName);
        orcidProfile.getOrcidBio().getPersonalDetails().getFamilyName().setContent(lastName);
        orcidProfile.getOrcidBio().getPersonalDetails().setOtherNames(buildOtherNamesFromDelimitedString(otherNamesDelimited));
        orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(Visibility.fromValue(otherNamesVisibility));
        orcidProfile.getOrcidBio().setKeywords(buildKeywordsFromDelimitedString(keywordsDelimited));
        orcidProfile.getOrcidBio().getKeywords().setVisibility(Visibility.fromValue(keywordsVisibility));
        orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().setContent(isoCountryCode);
        orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().setVisibility(Visibility.fromValue(isoCountryVisibility));
        orcidProfile.getOrcidBio().getBiography().setContent(biography);
        orcidProfile.getOrcidBio().setResearcherUrls(persistResearcherInfo());
        orcidProfile.getOrcidBio().getResearcherUrls().setVisibility(StringUtils.isBlank(websiteUrlVisibility) ? null : Visibility.fromValue(websiteUrlVisibility));
        orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(email);
        orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setVisibility(Visibility.fromValue(emailVisibility));
        orcidProfile.getOrcidBio().setExternalIdentifiers(externalIdentifiers);
    }

    private String buildOtherNamesAsDelimitedString(OtherNames otherNames) {

        String otherNamesDelimted = null;
        if (otherNames != null) {
            List<String> allOtherNames = otherNames.getOtherNamesAsStrings();
            otherNamesDelimted = StringUtils.join(allOtherNames, DELIMITER);
        }
        return otherNamesDelimted != null ? otherNamesDelimted : "";
    }

    private OtherNames buildOtherNamesFromDelimitedString(String otherNamesDelimted) {

        OtherNames otherNames = new OtherNames();
        String[] otherNamesSplit = StringUtils.split(otherNamesDelimted, DELIMITER);
        if (otherNamesSplit != null) {

            for (int i = 0; i < otherNamesSplit.length; i++) {
                OtherName otherName = new OtherName(otherNamesSplit[i]);
                otherNames.getOtherName().add(otherName);
            }
        }
        otherNames.setVisibility(Visibility.fromValue(otherNamesVisibility));

        return otherNames;

    }

    private ResearcherUrls persistResearcherInfo() {
        ResearcherUrls allResearchersForSave = savedResearcherUrls != null ? savedResearcherUrls : new ResearcherUrls();
        if (StringUtils.isNotBlank(websiteUrl)) {
            List<ResearcherUrl> allUrls = allResearchersForSave.getResearcherUrl();
            ResearcherUrl researcherUrl = new ResearcherUrl(new Url(websiteUrl));
            if (StringUtils.isNotBlank(websiteUrlText)) {
                researcherUrl.setUrlName(new UrlName(websiteUrlText));
            }
            allUrls.add(researcherUrl);
        }
        // Tidy up deleted urls
        Iterator<ResearcherUrl> iterator = allResearchersForSave.getResearcherUrl().iterator();
        while (iterator.hasNext()) {
            ResearcherUrl researcherUrl = iterator.next();
            if (researcherUrl.getUrl() == null) {
                iterator.remove();
            }
        }
        return allResearchersForSave;
    }

    private String buildKeywordsAsDelimitedString(Keywords keywords) {

        String keywordsDelimited = null;
        if (keywords != null) {
            List<String> allOtherNames = keywords.getKeywordsAsStrings();
            keywordsDelimited = StringUtils.join(allOtherNames, DELIMITER);
        }
        return keywordsDelimited != null ? keywordsDelimited : "";
    }

    private Keywords buildKeywordsFromDelimitedString(String otherNamesDelimted) {

        Keywords keywords = new Keywords();
        String[] keywordsSplit = StringUtils.split(otherNamesDelimted, DELIMITER);
        Set<Keyword> allKeywords = new java.util.HashSet<Keyword>();
        if (keywordsSplit != null) {

            for (int i = 0; i < keywordsSplit.length; i++) {
                Keyword keyword = new Keyword(keywordsSplit[i]);
                allKeywords.add(keyword);
            }
        }
        keywords.getKeyword().addAll(allKeywords);
        keywords.setVisibility(Visibility.fromValue(keywordsVisibility));

        return keywords;

    }

}
