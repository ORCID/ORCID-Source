package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.common.manager.EventManager;
import org.orcid.core.common.manager.SummaryManager;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.read_only.*;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.EventType;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PublicRecord;
import org.orcid.pojo.ajaxForm.AddressForm;
import org.orcid.pojo.ajaxForm.AddressesForm;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.ExternalIdentifiersForm;
import org.orcid.pojo.ajaxForm.KeywordsForm;
import org.orcid.pojo.ajaxForm.NamesForm;
import org.orcid.pojo.ajaxForm.OtherNamesForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.WebsitesForm;
import org.orcid.pojo.summary.RecordSummaryPojo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PublicRecordController extends BaseWorkspaceController {
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "personalDetailsManagerReadOnlyV3")
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Resource(name = "addressManagerReadOnlyV3")
    private AddressManagerReadOnly addressManagerReadOnly;

    @Resource(name = "profileKeywordManagerReadOnlyV3")
    private ProfileKeywordManagerReadOnly keywordManagerReadOnly;

    @Resource(name = "researcherUrlManagerReadOnlyV3")
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Resource(name = "externalIdentifierManagerReadOnlyV3")
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Resource(name = "profileEmailDomainManagerReadOnly")
    private ProfileEmailDomainManagerReadOnly profileEmailDomainManagerReadOnly;

    @Resource
    private EventManager eventManager;
    
    @Resource
    private SummaryManager summaryManager;
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/public-record.json", method = RequestMethod.GET)
    public @ResponseBody
    PublicRecord getPublicRecord(@PathVariable("orcid") String orcid) {
        PublicRecord publicRecord = new PublicRecord();
        boolean isDeprecated = false;

        try {
            if (Features.EVENTS.isActive()) {
                eventManager.createEvent(EventType.PUBLIC_PAGE, null);
            }
            // Check if the profile is deprecated or locked
            orcidSecurityManager.checkProfile(orcid);
        } catch (LockedException | DeactivatedException e) {
            publicRecord.setDisplayName(localeManager.resolveMessage("public_profile.deactivated.given_names") + " "
                    + localeManager.resolveMessage("public_profile.deactivated.family_name"));
            return publicRecord;
        } catch (OrcidNotClaimedException e) {
            publicRecord.setDisplayName(localeManager.resolveMessage("orcid.reserved_for_claim"));
            return publicRecord;
        } catch (OrcidDeprecatedException e) {
            isDeprecated = true;
        } catch (OrcidNoResultException e) {
            return publicRecord;
        }

        publicRecord = getRecord(orcid);
        if (isDeprecated) {
            // If deprecated be sure to remove all fields
            publicRecord.setBiography(null);
            publicRecord.setOtherNames(null);
            publicRecord.setCountries(null);
            publicRecord.setKeyword(null);
            publicRecord.setEmails(null);
            publicRecord.setExternalIdentifier(null);
        }

        // If the id belongs to a group the name field is removed
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (OrcidType.GROUP.name().equals(profile.getOrcidType())) {
            publicRecord.setDisplayName(null);
        }

        return publicRecord;
    }

    public @ResponseBody
    PublicRecord getRecord(String orcid) {
        PublicRecord publicRecord = new PublicRecord();

        PersonalDetails publicPersonalDetails = personalDetailsManagerReadOnly.getPublicPersonalDetails(orcid);
        // Fill personal details
        if (publicPersonalDetails != null) {
            // Get display name
            String displayName = "";

            if (publicPersonalDetails.getName() != null) {
                Name name = publicPersonalDetails.getName();
                if (name.getVisibility().equals(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC)) {
                    displayName = getDisplayName(name);
                    publicRecord.setNames(NamesForm.valueOf(name));
                }
            }

            if (!PojoUtil.isEmpty(displayName)) {
                // <Published Name> (<ORCID iD>) - ORCID | Connecting Research
                // and Researchers
                publicRecord.setTitle(displayName + " (" + orcid + ") - " + getMessage("layout.public-layout.title"));
                publicRecord.setDisplayName(displayName);
            }

            // Get biography
            if (publicPersonalDetails.getBiography() != null) {
                Biography bio = publicPersonalDetails.getBiography();
                if (org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(bio.getVisibility()) && !PojoUtil.isEmpty(bio.getContent())) {
                    publicRecord.setBiography(BiographyForm.valueOf(bio));
                }
            }

            // Fill other names
            OtherNames publicOtherNames = publicPersonalDetails.getOtherNames();
            if (publicOtherNames != null && publicOtherNames.getOtherNames() != null) {
                publicOtherNames.getOtherNames().removeIf(otherName -> !org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(otherName.getVisibility()));
            }
            publicRecord.setOtherNames(OtherNamesForm.valueOf(publicOtherNames));
        }

        // Fill country
        Addresses publicAddresses;
        publicAddresses = addressManagerReadOnly.getPublicAddresses(orcid);
        if (publicAddresses != null && publicAddresses.getAddress() != null) {
            AddressesForm form = AddressesForm.valueOf(publicAddresses);
            // Set country name
            if (form != null && form.getAddresses() != null) {
                Map<String, String> countries = retrieveIsoCountries();
                for (AddressForm addressForm : form.getAddresses()) {
                    addressForm.setCountryName(countries.get(addressForm.getIso2Country().getValue().name()));
                }
            }
            publicRecord.setCountries(form);
        }

        // Fill keywords
        Keywords publicKeywords;
        publicKeywords = keywordManagerReadOnly.getPublicKeywords(orcid);
        publicRecord.setKeyword(KeywordsForm.valueOf(publicKeywords));

        // Fill researcher urls
        ResearcherUrls publicResearcherUrls;
        publicResearcherUrls = researcherUrlManagerReadOnly.getPublicResearcherUrls(orcid);

        publicRecord.setWebsite(WebsitesForm.valueOf(publicResearcherUrls));

        // Fill emails
        Emails publicEmails;
        publicEmails = emailManagerReadOnly.getPublicEmails(orcid);

        Emails filteredEmails = new Emails();
        filteredEmails.setEmails(new ArrayList<>(publicEmails.getEmails().stream().filter(Email::isVerified).collect(Collectors.toList())));

        // Fill email domains
        List<ProfileEmailDomainEntity> emailDomains = null;
        if (Features.EMAIL_DOMAINS.isActive()) {
            emailDomains = profileEmailDomainManagerReadOnly.getPublicEmailDomains(orcid);
        }

        org.orcid.pojo.ajaxForm.Emails emails = org.orcid.pojo.ajaxForm.Emails.valueOf(filteredEmails, emailDomains);
        // Old emails are missing the source name and id -- assign the user as the source
        if (emails.getEmails() != null) {
            for (org.orcid.pojo.ajaxForm.Email email: emails.getEmails()) {
                if (email.getSource() == null && email.getSourceName() == null) {
                    email.setSource(orcid);
                    email.setSourceName(publicRecord.getDisplayName());
                }
            }
        }

        publicRecord.setEmails(emails);

        // Fill external identifiers
        PersonExternalIdentifiers publicPersonExternalIdentifiers;
        publicPersonExternalIdentifiers = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid);

        publicRecord.setExternalIdentifier(ExternalIdentifiersForm.valueOf(publicPersonExternalIdentifiers));

        Long lastModifiedTime = getLastModifiedTime(orcid);

        publicRecord.setLastModifiedTime(new java.util.Date(lastModifiedTime));

        return publicRecord;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/summary.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RecordSummaryPojo getSummaryRecord(@PathVariable("orcid") String orcid) {
        RecordSummaryPojo recordSummary = new RecordSummaryPojo();
        try {
            // Check if the profile is deprecated or locked
            orcidSecurityManager.checkProfile(orcid);
        } catch (LockedException | DeactivatedException e) {
            if (e instanceof LockedException) {
                recordSummary.setStatus("locked");
            } else {
                recordSummary.setStatus("deactivated");
            }
            recordSummary.setName(localeManager.resolveMessage("public_profile.deactivated.given_names") + " "
                    + localeManager.resolveMessage("public_profile.deactivated.family_name"));
            return recordSummary;
        } catch (OrcidNotClaimedException e) {
            recordSummary.setName(localeManager.resolveMessage("orcid.reserved_for_claim"));
            return recordSummary;
        } catch (OrcidDeprecatedException e) {
            recordSummary.setStatus("deprecated");
            recordSummary.setEmploymentAffiliations(null);
            recordSummary.setProfessionalActivities(null);
            recordSummary.setExternalIdentifiers(null);
            return recordSummary;
        } catch (OrcidNoResultException e) {
            return recordSummary;
        }

        // If not exceptions found, set the record as active and generate the summary        
        return summaryManager.getRecordSummaryPojo(orcid);
    }
    
    private String getDisplayName(Name name) {
        String displayName = null;
        if (name.getCreditName() != null && !PojoUtil.isEmpty(name.getCreditName().getContent())) {
            displayName = name.getCreditName().getContent();
        } else {
            if (name.getGivenNames() != null && !PojoUtil.isEmpty(name.getGivenNames().getContent())) {
                displayName = name.getGivenNames().getContent() + " ";
            }
            if (name.getFamilyName() != null && !PojoUtil.isEmpty(name.getFamilyName().getContent())) {
                displayName += name.getFamilyName().getContent();
            }
        }
        return displayName;
    }

    private Long getLastModifiedTime(String orcid) {
        return profileEntityManager.getLastModified(orcid);
    }    
}
