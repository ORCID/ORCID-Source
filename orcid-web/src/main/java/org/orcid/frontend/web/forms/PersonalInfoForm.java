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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ShortDescription;
import org.orcid.jaxb.model.message.Visibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Will Simpson
 */
public class PersonalInfoForm {

    private boolean masterPublic;

    private String givenNames;

    private String familyName;

    private String creditName;

    private String orcid;

    private boolean otherNamesPublic;

    private Map<String, String> otherNames = new HashMap<String, String>();

    private List<String> selectedOtherNames;

    private boolean orcidUrlPublic;

    private boolean researcherUrlsPublic;

    private Map<String, String> researcherUrls = new HashMap<String, String>();

    private List<String> selectedResearcherUrls;

    private String emailVisibility;

    private String email;

    private boolean worksPublic;

    private boolean authorUrlsOnMyPublicationsPublic;

    private Map<String, String> keywords = new HashMap<String, String>();

    private List<String> selectedKeywords;

    private boolean subjectsPublic;

    private List<String> availableRemainingSubjects;

    private Map<String, String> availableRemainingSubjectMap = new HashMap<String, String>();

    private Map<String, String> researcherSubjects = new HashMap<String, String>();

    private List<String> selectedResearcherSubjects = new ArrayList<String>();

    private boolean shortDescriptionPublic;

    private String shortDescription;

    public PersonalInfoForm() {
    }

    public PersonalInfoForm(OrcidProfile orcidProfile, Map<String, String> availableSubjects) {
        availableRemainingSubjectMap = availableSubjects;
        orcid = orcidProfile.getOrcid().getValue();

        OrcidBio orcidBio = orcidProfile.getOrcidBio();
        PersonalDetails personalDetails = null;
        ContactDetails contactDetails = null;
        Keywords keywords = null;
        // Get current researcher subjects from profile     
        Biography profileShortDescription = null;
        if (orcidBio != null) {
            personalDetails = orcidBio.getPersonalDetails();

            contactDetails = orcidBio.getContactDetails();
            keywords = orcidBio.getKeywords();
            profileShortDescription = orcidBio.getBiography();
        }

        GivenNames profileGivenNames = personalDetails != null ? personalDetails.getGivenNames() : null;
        givenNames = profileGivenNames == null ? null : profileGivenNames.getContent();

        //        Visibility personalVisibility = (personalDetails != null && personalDetails.getVisibility() != null) ? personalDetails.getVisibility()
        //                : OrcidVisibilityDefaults.PERSONAL_DETAILS_DEFAULT.getVisibility();
        //
        //        masterPublic = personalVisibility.equals(Visibility.PUBLIC);

        FamilyName profileFamilyName = personalDetails != null ? personalDetails.getFamilyName() : null;
        familyName = profileFamilyName == null ? null : profileFamilyName.getContent();

        CreditName profileCreditName = personalDetails != null ? personalDetails.getCreditName() : null;
        creditName = profileCreditName == null ? null : profileCreditName.getContent();

        Visibility otherNameVisibility = (personalDetails != null && personalDetails.getOtherNames() != null && personalDetails.getOtherNames().getVisibility() != null) ? personalDetails
                .getOtherNames().getVisibility()
                : OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT.getVisibility();
        otherNamesPublic = otherNameVisibility.equals(Visibility.PUBLIC);

        if (personalDetails != null && personalDetails.getOtherNames() != null) {
            for (OtherName otherName : personalDetails.getOtherNames().getOtherName()) {
                otherNames.put(otherName.getContent(), otherName.getContent());
            }
        }
        ResearcherUrls researcherUrls = personalDetails != null ? orcidBio.getResearcherUrls() : null;
        if (researcherUrls != null) {
            for (ResearcherUrl researcherUrl : researcherUrls.getResearcherUrl()) {
                //TODO JB check this!!
                //  this.researcherUrls.put(url.getValue(), url.getValue());
            }
        }
        /*
        Visibility researcherUrlVisibility = (personalDetails != null && orcidBio.getResearcherUrls() != null && orcidBio.getResearcherUrls()
                .getVisibility() != null) ? orcidBio().getVisibility() : OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility();
        researcherUrlsPublic = researcherUrlVisibility.equals(Visibility.PUBLIC);
         */
        email = (contactDetails != null && contactDetails.getEmail() != null) ? contactDetails.retrievePrimaryEmail().getValue() : null;
        emailVisibility = (contactDetails != null && contactDetails.getEmail() != null && contactDetails.retrievePrimaryEmail().getVisibility() != null) ? contactDetails
                .retrievePrimaryEmail().getVisibility().toString() : OrcidVisibilityDefaults.PRIMARY_EMAIL_DEFAULT.getVisibility().toString();

        //   Visibility contactDetailsVisibility = (contactDetails != null && contactDetails.getEmail() != null) ? contactDetails.getEmail().getVisibility()
        // : OrcidVisibilityDefaults.CONTACT_DETAIL_DEFAULT.getVisibility();
        //emailVisibility = contactDetailsVisibility.value();

        // Get keywords from profile

        if (keywords != null) {
            for (Keyword keyword : keywords.getKeyword()) {
                this.keywords.put(keyword.getContent(), keyword.getContent());
            }
        }

        shortDescription = profileShortDescription == null ? null : profileShortDescription.getContent();

        Visibility shortDescritionVisibility = (profileShortDescription != null && profileShortDescription.getVisibility() != null) ? profileShortDescription
                .getVisibility() : OrcidVisibilityDefaults.SHORT_DESCRIPTION_DEFAULT.getVisibility();
        shortDescriptionPublic = shortDescritionVisibility.equals(Visibility.PUBLIC);

    }

    public OrcidProfile getOrcidProfile() {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcid(orcid);
        OrcidBio bio = new OrcidBio();
        profile.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();
        bio.setPersonalDetails(personalDetails);
        personalDetails.setGivenNames(new GivenNames(givenNames));
        personalDetails.setFamilyName(new FamilyName(familyName));
        personalDetails.setCreditName(new CreditName(creditName));
        //personalDetails.setVisibility(masterPublic ? Visibility.PUBLIC : Visibility.LIMITED);

        OtherNames otherNames = new OtherNames();
        otherNames.setVisibility(otherNamesPublic ? Visibility.PUBLIC : Visibility.LIMITED);
        personalDetails.setOtherNames(otherNames);
        if (selectedOtherNames != null) {
            Set<String> nonDupeOtherNames = new HashSet<String>();
            nonDupeOtherNames.addAll(selectedOtherNames);
            for (String otherName : nonDupeOtherNames) {
                otherNames.addOtherName(otherName);
            }
        }

        //        ResearcherUrls researcherUrls = new ResearcherUrls();
        //        researcherUrls.setVisibility(researcherUrlsPublic ? Visibility.PUBLIC : Visibility.LIMITED);
        //        bio.setResearcherUrls(researcherUrls);
        //        if (selectedResearcherUrls != null && !selectedResearcherUrls.isEmpty()) {
        //            for (String url : selectedResearcherUrls) {
        //                researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url(url)));
        //            }
        //        }

        ContactDetails contactDetails = new ContactDetails();
        Visibility emailVisibility = Visibility.fromValue(this.emailVisibility);
        bio.setContactDetails(contactDetails);
        Email emailObject = new Email(email);
        emailObject.setVisibility(emailVisibility);
        contactDetails.addOrReplacePrimaryEmail(emailObject);
        Keywords keywords = new Keywords();
        if (selectedKeywords != null && !selectedKeywords.isEmpty()) {
            for (String keyword : selectedKeywords) {
                keywords.getKeyword().add(new Keyword(keyword));
            }
        }

        bio.setKeywords(keywords);

        ShortDescription description = new ShortDescription();
        // description.setVisibility(shortDescriptionPublic ? Visibility.PUBLIC : Visibility.LIMITED);
        bio.setBiography(new Biography(shortDescription));
        if (!StringUtils.isEmpty(shortDescription)) {
            description.setContent(shortDescription);
        }

        return profile;
    }

    public boolean isMasterPublic() {
        return masterPublic;
    }

    public void setMasterPublic(boolean masterPublic) {
        this.masterPublic = masterPublic;
    }

    @NotBlank
    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    @NotBlank
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @NotBlank
    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public boolean isOtherNamesPublic() {
        return otherNamesPublic;
    }

    public void setOtherNamesPublic(boolean otherNamesPublic) {
        this.otherNamesPublic = otherNamesPublic;
    }

    public Map<String, String> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(Map<String, String> otherNames) {
        this.otherNames = otherNames;
    }

    public List<String> getSelectedOtherNames() {
        return selectedOtherNames;
    }

    public void setSelectedOtherNames(List<String> selectedOtherNames) {
        this.selectedOtherNames = selectedOtherNames;
    }

    public boolean isOrcidUrlPublic() {
        return orcidUrlPublic;
    }

    public void setOrcidUrlPublic(boolean orcidUrlPublic) {
        this.orcidUrlPublic = orcidUrlPublic;
    }

    public boolean isResearcherUrlsPublic() {
        return researcherUrlsPublic;
    }

    public void setResearcherUrlsPublic(boolean researcherUrlsPublic) {
        this.researcherUrlsPublic = researcherUrlsPublic;
    }

    public Map<String, String> getResearcherUrls() {
        return researcherUrls;
    }

    public void setResearcherUrls(Map<String, String> researcherUrls) {
        this.researcherUrls = researcherUrls;
    }

    public List<String> getSelectedResearcherUrls() {
        return selectedResearcherUrls;
    }

    public void setSelectedResearcherUrls(List<String> selectedResearcherUrls) {
        this.selectedResearcherUrls = selectedResearcherUrls;
    }

    public String getEmailVisibility() {
        return emailVisibility;
    }

    public void setEmailVisibility(String emailVisibility) {
        this.emailVisibility = emailVisibility;
    }

    @NotBlank
    @org.hibernate.validator.constraints.Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isWorksPublic() {
        return worksPublic;
    }

    public void setWorksPublic(boolean worksPublic) {
        this.worksPublic = worksPublic;
    }

    public boolean isAuthorUrlsOnMyPublicationsPublic() {
        return authorUrlsOnMyPublicationsPublic;
    }

    public void setAuthorUrlsOnMyPublicationsPublic(boolean authorUrlsOnMyPublicationsPublic) {
        this.authorUrlsOnMyPublicationsPublic = authorUrlsOnMyPublicationsPublic;
    }

    public Map<String, String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Map<String, String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getSelectedKeywords() {
        return selectedKeywords;
    }

    public void setSelectedKeywords(List<String> selectedKeywords) {
        this.selectedKeywords = selectedKeywords;
    }

    public boolean isSubjectsPublic() {
        return subjectsPublic;
    }

    public void setSubjectsPublic(boolean subjectsPublic) {
        this.subjectsPublic = subjectsPublic;
    }

    public List<String> getAvailableRemainingSubjects() {
        return availableRemainingSubjects;
    }

    public void setAvailableRemainingSubjects(List<String> availableRemainingSubjects) {
        this.availableRemainingSubjects = availableRemainingSubjects;
    }

    public Map<String, String> getResearcherSubjects() {
        return researcherSubjects;
    }

    public void setResearcherSubjects(Map<String, String> researcherSubjects) {
        this.researcherSubjects = researcherSubjects;
    }

    public List<String> getSelectedResearcherSubjects() {
        return selectedResearcherSubjects;
    }

    public void setSelectedResearcherSubjects(List<String> selectedResearcherSubjects) {
        this.selectedResearcherSubjects = selectedResearcherSubjects;
    }

    public boolean isShortDescriptionPublic() {
        return shortDescriptionPublic;
    }

    public void setShortDescriptionPublic(boolean shortDescriptionPublic) {
        this.shortDescriptionPublic = shortDescriptionPublic;
    }

    @Length(max = 300)
    public String getShortDescription() {
        return shortDescription;
    }

    public Map<String, String> getAvailableRemainingSubjectMap() {
        return availableRemainingSubjectMap;
    }

    public void setAvailableRemainingSubjectMap(Map<String, String> availableRemainingSubjectMap) {
        this.availableRemainingSubjectMap = availableRemainingSubjectMap;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
